---
title: "tools"
layout: archive
permalink: categories/tools
author_profile: true
sidebar_main: true
---

{% assign posts = site.categories['tools'] %}
{% for post in posts %} {% include archive-single.html type=page.entries_layout %} {% endfor %}