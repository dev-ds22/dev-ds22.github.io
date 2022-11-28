---
title: "elk"
layout: archive
permalink: categories/elk
author_profile: true
sidebar_main: true
---

{% assign posts = site.categories['elk'] %}
{% for post in posts %} {% include archive-single.html type=page.entries_layout %} {% endfor %}