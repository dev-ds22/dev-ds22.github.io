---
title: "issue"
layout: archive
permalink: categories/issue
author_profile: true
sidebar_main: true
---

{% assign posts = site.categories.issue %}
{% for post in posts %} {% include archive-single.html type=page.entries_layout %} {% endfor %}