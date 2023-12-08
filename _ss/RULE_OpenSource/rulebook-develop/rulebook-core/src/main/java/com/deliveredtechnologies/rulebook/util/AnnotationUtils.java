package com.deliveredtechnologies.rulebook.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AnnotationUtils is a utility class for annotations.
 */
public final class AnnotationUtils {
  private AnnotationUtils() { }

  /**
   * Method getAnnotatedFields gets the fields annotated of a specific type from the class and its parent classes. <br/>
   * The List is in order of closest parent => current obj fields, parent obj fields, etc.
   * @param annotation      the annotation type
   * @param clazz           the class that is expected to be annotated
   * @return                a {@link Set} of {@link Field} objects annotated with the annotatedClass
   */
  @SuppressWarnings("unchecked")
  public static List<Field> getAnnotatedFields(Class annotation, Class clazz) {
    if (clazz == Object.class) {
      return new ArrayList<>();
    }
    List<Field> fields = (List<Field>)Arrays.stream(clazz.getDeclaredFields())
            .filter(field -> field.getAnnotation(annotation) != null)
            .collect(Collectors.toList());
    if (clazz.getSuperclass() != null) {
      fields.addAll(getAnnotatedFields(annotation, clazz.getSuperclass()));
    }
    return fields;
  }

  /**
   * Method getAnnotatedField gets the first annotated field of the type of annotation specified.
   * @param annotation      the type of the annotation
   * @param clazz           the annotated class
   * @return                the first annotated field found in clazz of the type annotation
   */
  public static Optional<Field> getAnnotatedField(Class annotation, Class clazz) {
    List<Field> fields = getAnnotatedFields(annotation, clazz);
    return Optional.ofNullable(fields.size() > 0 ? fields.get(0) : null);
  }

  /**
   * Method getAnnotatedMethods gets the methods annotated of a specific type from the class and its parent
   * classes.<br/> The List is in order of closest parent => current obj methods, parent obj methods, etc.
   * @param annotation      the type of the annotation
   * @param clazz           the annotated class
   * @return                a List of Methods that have been annotated using annotation in the class clazz
   */
  @SuppressWarnings("unchecked")
  public static List<Method> getAnnotatedMethods(Class annotation, Class clazz) {
    List<Method> methods = new ArrayList<>();
    if (clazz == Object.class) {
      return methods;
    }
    methods.addAll((List<Method>)
        Arrays.stream(clazz.getDeclaredMethods())
        .filter(field -> field.getAnnotation(annotation) != null)
        .collect(Collectors.toList()));
    methods.addAll(getAnnotatedMethods(annotation, clazz.getSuperclass()));
    return methods;
  }

  /**
   * Method getAnnotatedMethod the first annotated method of the type of annotation specified.
   * @param annotation      the type of the annotation
   * @param clazz           the annotated class
   * @return                the first annotated field found in clazz of the type annotation
   */
  public static Optional<Method> getAnnotatedMethod(Class annotation, Class clazz) {
    List<Method> methods = getAnnotatedMethods(annotation, clazz);
    return Optional.ofNullable(methods.size() > 0 ? methods.get(0) : null);
  }

  /**
   * Method getAnnotation returns the annotation on a class or its parent annotation.
   * @param clazz       the annotated class
   * @param annotation  the annotation to find
   * @param <A>         the type of the annotation
   * @return            the actual annotation used or null if it doesn't exist
   */
  @SuppressWarnings("unchecked")
  public static <A extends Annotation> A getAnnotation(Class<A> annotation, Class<?> clazz) {
    return Optional.ofNullable(clazz.getAnnotation(annotation)).orElse((A)
      Arrays.stream(clazz.getDeclaredAnnotations())
        .flatMap(anno -> Arrays.stream(anno.getClass().getInterfaces())
          .flatMap(iface -> Arrays.stream(iface.getDeclaredAnnotations())))
        .filter(annotation::isInstance)
        .findFirst().orElse(null)
    );
  }
}
