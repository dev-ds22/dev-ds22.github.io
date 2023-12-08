package org.evrete.spi.minimal;

import org.evrete.api.*;
import org.evrete.runtime.compiler.CompilationException;
import org.evrete.util.NextIntSupplier;
import org.evrete.util.StringLiteralRemover;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

class EvaluatorCompiler {
    private static final String JAVA_EVALUATOR_TEMPLATE = "package %s;\n" +
            "%s\n" +
            "\n" +
            "public final class %s extends %s {\n" +
            "    public static final java.lang.invoke.MethodHandle HANDLE;\n" +
            "\n" +
            "    static {\n" +
            "        try {\n" +
            "            HANDLE = java.lang.invoke.MethodHandles.lookup().findStatic(%s.class, \"__$test\", java.lang.invoke.MethodType.methodType(boolean.class, %s));\n" +
            "        } catch (Exception e) {\n" +
            "            throw new IllegalStateException(e);\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    private static boolean __$testInner(%s) {\n" +
            "        return %s;\n" +
            "    }\n" +
            "\n" +
            "    public static boolean __$test(%s) {\n" +
            "        return %s\n" +
            "    }\n\n" +
            "    //IMPORTANT LINE BELOW, IT IS USED IN SOURCE/SIGNATURE COMPARISON\n" +
            "    //%s\n" +
            "}\n";


    private final static NextIntSupplier javaClassCounter = new NextIntSupplier();

    private final RuntimeContext<?> context;

    EvaluatorCompiler(RuntimeContext<?> context) {
        this.context = context;
    }

    private MethodHandle compileExpression(String classJavaSource) throws CompilationException {
        try {
            Class<?> compiledClass = context.getSourceCompiler().compile(classJavaSource);
            return (MethodHandle) compiledClass.getDeclaredField("HANDLE").get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new CompilationException(e, classJavaSource);
        }
    }

    Evaluator buildExpression(String baseClassName, StringLiteralRemover remover, String strippedExpression, List<ConditionStringTerm> terms, Imports imports) throws CompilationException {
        int accumulatedShift = 0;
        StringJoiner argClasses = new StringJoiner(", ");
        StringJoiner argTypes = new StringJoiner(", ");
        StringJoiner argCasts = new StringJoiner(", ");
        int castVarIndex = 0;
        StringJoiner methodArgs = new StringJoiner(", ");
        List<ConditionStringTerm> uniqueReferences = new ArrayList<>();
        List<FieldReference> descriptorBuilder = new ArrayList<>();

        for (ConditionStringTerm term : terms) {
            String original = strippedExpression.substring(term.start + accumulatedShift, term.end + accumulatedShift);
            String javaArgVar = term.varName;
            String before = strippedExpression.substring(0, term.start + accumulatedShift);
            String after = strippedExpression.substring(term.end + accumulatedShift);
            strippedExpression = before + javaArgVar + after;
            accumulatedShift += javaArgVar.length() - original.length();


            if (!uniqueReferences.contains(term)) {
                //Build the reference
                descriptorBuilder.add(term);
                //Prepare the corresponding source code vars
                Class<?> fieldType = term.field().getValueType();

                argTypes.add(term.type().getType().getName() + "/" + term.field().getName());
                argCasts.add("(" + fieldType.getCanonicalName() + ") values.apply(" + castVarIndex + ")");
                argClasses.add(fieldType.getName() + ".class");
                methodArgs.add(fieldType.getCanonicalName() + " " + javaArgVar);
                castVarIndex++;
                // Mark as processed
                uniqueReferences.add(term);
            }
        }

        // Adding imports
        StringBuilder importsBuilder = new StringBuilder(1024);
        imports.asJavaImportStatements(importsBuilder);


        String replaced = remover.unwrapLiterals(strippedExpression);

        String pkg = this.getClass().getPackage().getName() + ".compiled";
        String clazz = "Condition" + javaClassCounter.next();
        String javaClassSource = String.format(
                JAVA_EVALUATOR_TEMPLATE,
                pkg,
                importsBuilder,
                clazz,
                baseClassName,
                clazz,
                IntToValue.class.getName() + ".class",
                methodArgs,
                replaced,
                IntToValue.class.getName() + " values",
                "__$testInner(" + argCasts + ");",
                "fields in use: " + argTypes
        );

        String comparableClassSource = javaClassSource.replaceAll(clazz, "CLASS_STUB");

        FieldReference[] descriptor = descriptorBuilder.toArray(FieldReference.ZERO_ARRAY);
        if (descriptor.length == 0)
            throw new IllegalArgumentException("No field references were resolved in the '" + strippedExpression + "'");
        MethodHandle methodHandle = compileExpression(javaClassSource);
        return new CompiledEvaluator(methodHandle, remover.getOriginal(), javaClassSource, comparableClassSource, descriptor);

    }

    static class CompiledEvaluator implements Evaluator {
        private final FieldReference[] descriptor;
        private final MethodHandle methodHandle;
        private final String originalCondition;
        private final String javaClassSource;
        private final String comparableClassSource;

        CompiledEvaluator(MethodHandle methodHandle, String originalCondition, String javaClassSource, String comparableClassSource, FieldReference[] descriptor) {
            this.descriptor = descriptor;
            this.originalCondition = originalCondition;
            this.javaClassSource = javaClassSource;
            this.comparableClassSource = comparableClassSource;
            this.methodHandle = methodHandle;
        }


        String getSource() {
            return javaClassSource;
        }

        @Override
        public int compare(Evaluator other) {
            if (other instanceof CompiledEvaluator) {
                CompiledEvaluator o = (CompiledEvaluator) other;
                if (o.descriptor.length == 1 && this.descriptor.length == 1 && o.comparableClassSource.equals(this.comparableClassSource)) {
                    return RELATION_EQUALS;
                }
            }

            return Evaluator.super.compare(other);
        }

        @Override
        public FieldReference[] descriptor() {
            return descriptor;
        }

        @Override
        public boolean test(IntToValue values) {
            try {
                return (boolean) methodHandle.invoke(values);
            } catch (SecurityException t) {
                throw t;
            } catch (Throwable t) {
                Object[] args = new Object[descriptor.length];
                for (int i = 0; i < args.length; i++) {
                    args[i] = values.apply(i);
                }
                throw new IllegalStateException("Evaluation exception at '" + originalCondition + "', arguments: " + Arrays.toString(descriptor) + " -> " + Arrays.toString(args), t);
            }
        }

        @Override
        public String toString() {
            return "\"" + originalCondition + "\"";
        }
    }

}
