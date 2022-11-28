---
title: "common"
layout: archive
permalink: categories/common
author_profile: true
sidebar_main: true
---

{% assign posts = site.categories['common'] %}
{% for post in posts %} {% include archive-single.html type=page.entries_layout %} {% endfor %}