---
title: "note"
layout: archive
permalink: categories/note
author_profile: true
sidebar_main: true
---

{% assign posts = site.categories['note'] %}
{% for post in posts %} {% include archive-single.html type=page.entries_layout %} {% endfor %}