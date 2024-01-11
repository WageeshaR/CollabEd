# CollabEd
CollabEd (**Collab**orative, Insightful, **Ed**ucational Networking with Peers) is designed to be a regulated environment for research teams, higher educational institutions and various other kinds of collaborative human workforce to efficiently network among peers in closed or open environments whilst also providing a medium for incremental knowledge generation via open social learning methodologies.

This repository contains the core backend Java application, built with the power of Spring Boot.

**Source Code**
* Java 17
* Spring 6 / Spring Boot 3
* MongoDB 4 (core database)
* Cassandra 4 (stores analytics data)
* H2 DB (temporarily stores batch job data because Spring Batch does not support Mongo or Cassandra for Job Repositories)

### Disclaimer: A core database switch is expected to happen in the foreseeable future.

I expect to migrate the current Mongo database to a Spring Batch compatible SQL DB because of burden of maintaining 3 databases.
Currently, major candidates are MySQL and Postgres.

(currently working on system architecture documentation and will be updated here once it's upto date)

## CollabEd Eco-system ##
* **CollabEd** (core)
  * This is the core backend of CollabEd architecture
  * Please DO NOT attempt to push to MAIN branch (it's protected but rules are not enforced due to this being a personal account)
  * Consisting of a monolithic, n-tier application with a set of service components
  * Simple JWT authentication - will be extended with OAuth2 for SSO
  * **CEGateway**, a generic interface for custom gateway implementations (e.g., a PaymentGateway)
    * Implementation has started for _SimpleIntelGateway_, a simple HTTP gateway for _CEIntel AI_ engine (currently a private repo)
  * **Spring Batch** runs data pre-processing jobs and pushes to a Cassandra cluster for analytics and training by CEIntel engine.
  * Documentation with Swagger + OpenAPI
  * This is aimed to be a TDD approach with a reasonable test case coverage so far - every feature PR is expected to be bug free with necessary Unit Test coverage


* **CEClient** (browser application) - https://github.com/WageeshaR/CEClient
  * Vite-React and TypeScript(ed) web browser front-end of CollabEd
  * This is largely not-developed yet (only signin/up functionalities are developed)
  * Uses OpenAPI-generated _api_ module for type-matching and client-server communication - it helps TypeScript code clean and bug-free during API calls


* **CEIntel** (AI engine)
  * As of now, content moderation NLP algorithms are being developed using a _transformer_ model with [Google's BERT](https://en.wikipedia.org/wiki/BERT_(language_model)) text encoder
  * Expectations align with building a language translation feature in coming months
  * Consisting of 2 primary components:
    * Preprocessor: calls for a preprocessing job on a batch of raw data in Cassandra cluster. Calling can be either to in-memory Pandas-based preprocessor or external Spark pipeline, dynamically based on the nature of data. Spark implementation details are not yet finalised. 
    * Brain: runs actual AI/ML operations, primarily for natural languages


* **CEDeploy** (deployment configurations)
  * CollabEd core and data sources are currently setup with CI/CD in AWS with ECS clusters and an EC2 instance for Mongo (AWS's Mongo support is below par)
  * ECS task definitions and other EC2 configurations are in https://github.com/WageeshaR/CEDeploy
  * CI/CD for core is located under `.github` directory here as github workflows
  * CEClient is deployed using AWS Amplify
  * _Servers are not running as the project is still under development_.



