# Edith Gonzalez - Computer Science ePortfolio

[![GitHub Pages](https://img.shields.io/badge/Website-Live-brightgreen.svg)](https://egonzalez-3.github.io/)

Welcome to my professional ePortfolio! This repository serves as the culmination of my Bachelor of Science in Computer Science program, with a concentration in Software Engineering, at Southern New Hampshire University. 

This portfolio showcases my growth as a software engineer through a series of enhanced technical artifacts. It highlights my proficiency in full-stack development, modern architectural patterns, algorithmic optimization, and my commitment to proactive software security.

## Table of Contents
* [Professional Self-Assessment](https://egonzalez-3.github.io/#professional-assessment)
* [Informal Code Review](https://egonzalez-3.github.io/#code-review)
  * [Software Engineering and Design](https://egonzalez-3.github.io/#software-design)
  * [Algorithms and Data Structures](https://egonzalez-3.github.io/#algorithms)
  * [Databases](https://egonzalez-3.github.io/#databases)

## Professional Self-Assessment
My professional self-assessment provides a holistic overview of my journey through the Computer Science program. It details my collaborative experiences, my communication strategies with stakeholders, and how my overarching security mindset informs my approach to software development. 

* [Read my Professional Self-Assessment Here](https://github.com/EGonzalez-3/EGonzalez-3.github.io/blob/main/Professional%20Self%20Assessment.md)

## Informal Code Review
Prior to enhancing the artifacts in this repository, I conducted a comprehensive code review to analyze technical debt, architectural flaws, and security vulnerabilities in my legacy code. 

* [Watch the Code Review Video](https://www.youtube.com/watch?v=iIvn76kXJkA&feature=youtu.be)

## Enhanced Artifacts

This repository contains three major artifacts, each significantly refactored to meet modern industry standards across key computer science disciplines. 

### Software Engineering and Design
**Android Inventory Tracking Application (Java, SQLite)**
Originally a legacy mobile application with tightly coupled logic, this project was completely refactored to adhere to clean architecture principles. 
* **Enhancements:** Transitioned the application to an MVVM (Model-View-ViewModel) architectural pattern, decoupling the UI from database operations using a Repository layer and LiveData.
* **Security:** Implemented strict Regular Expression (Regex) validation to actively sanitize user input and prevent SQL injection attacks.

### Algorithms and Data Structures
**Investment Portal Application (C++)**
A console-based financial management tool refactored to prioritize algorithmic efficiency and memory safety.
* **Enhancements:** Replaced inefficient linear data structures with an optimized Hash Map (`std::unordered_map`), drastically reducing data retrieval time complexity to O(1). 
* **Memory Management:** Eliminated manual memory handling and legacy raw pointers by implementing modern C++ smart pointers (`std::unique_ptr`) to natively prevent memory leaks. 
* **Security:** Integrated robust security algorithms, including a three-attempt account lockout mechanism and strict stream-state input validation to prevent buffer issues.

### Databases
**Animal Shelter Client/Server Dashboard (Python, MongoDB)**
A backend CRUD application upgraded to enforce strict data integrity within a schemaless NoSQL environment. 
* **Enhancements:** Developed complex server-side data processing by implementing aggregation pipelines (`$group`, `$sort`) and relational joins (`$lookup`) to connect multiple collections. Optimized query performance using compound database indexing.
* **Security:** Engineered strict native MongoDB JSON Schema validators to enforce type-casting and required fields, successfully immunizing the database against malformed payloads and NoSQL injection attacks.
