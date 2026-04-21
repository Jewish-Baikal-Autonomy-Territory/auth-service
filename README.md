# 📋 Auth Service

![CI](https://github.com/Jewish-Baikal-Autonomy-Territory/auth-service/actions/workflows/main.yml/badge.svg)
![Coverage](https://codecov.io/gh/Jewish-Baikal-Autonomy-Territory/auth-service/branch/main/graph/badge.svg)
![Go Version](https://img.shields.io/badge/java-25-00ADD8?logo=java)
![Docker](https://img.shields.io/docker/v/your-org/task-service?label=docker&logo=docker)
![License](https://img.shields.io/badge/license-Apache2.0-green)

A gRPC microservice responsible for authentication and session management within the Geo Assistant App — an application providing geofence management, AI-powered assistance, and task tracking for users.
This service handles the complete authentication lifecycle: user registration and login via basic credentials, federated sign-in through Google ID and Apple ID, token issuance and refresh, and session revocation — backed by PostgreSQL for persistence, Redis for session caching, and Kafka for publishing auth events.
