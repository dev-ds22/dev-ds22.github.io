---
title: "front"
layout: archive
permalink: categories/front
author_profile: true
sidebar_main: true
---

{% assign posts = site.categories['front'] %}
{% for post in posts %} {% include archive-single.html type=page.entries_layout %} {% endfor %}