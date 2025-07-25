# Selenium-TestNG-ExtentReport Hybrid Framework

A robust automation framework enabling parallel Selenium tests with TestNG and ExtentReports. Supports local, Grid, and cloud execution, multiple browsers and seamless CI/CD integration.

## Overview
This framework provides a clean, modular architecture for UI automation using Selenium WebDriver and TestNG. It features:

- Dynamic browser setup

- Retry logic for flaky selenium interactions

- Parallel execution support

- Excel-based data-driven testing strategies

- ExtentReports integration with parallel support, consolidated & per test case with steps, logs, screenshots.

- Separate log file for each test case stored alongside its report

- Configurable for Grid, Docker-Grid, or cloud (Azure) runs


## Setup & Configuration
1. Clone the repository
```
git clone https://github.com/Bala-murugan-tr/selenium-testng-extentreport.git
```
2. Edit src/test/resources/config.properties to suit your app environment

## Project Structure
```
├── LICENSE
├── README.md
├── pom.xml
├── .gitignore
├── docs                        # contains documentations
├── pipelines                   # contains azure pipeline yamls
└── src
    ├── main
        ├── java
            ├── base
            ├── exceptions      
            ├── actions     
            ├── config
            ├── context
            ├── engine
            ├── enums
            ├── interfaces      
            ├── utils       
            └── pages           
        └── resources
    └── test
        ├── java                # contains test classes
        └── resources
            ├── testdata        # contains excel files
            ├── testfiles       # contains files which may need for testing
            └── testsuite       # contains testng xmls

```


## Reporting & Logging
Each test case generates an individual ExtentReport HTML file

Screenshots are captured on failure and embedded in the report steps

A master ExtentReport summarizes the entire suite

Logs for each test case are written to a .log file saved alongside its HTML report

## Data-Driven Testing (Excel)
Excel-based test data can be organized in multiple ways:

`SINGLE`: all test cases data in one Excel file

`INDIVIDUAL`: one Excel file per test

`MODULAR`: separate excel files per module

`MODULAR_INDIVIDUAL`: one Excel file per test placed in each respective modules

Configure the strategy via `excel.strategy`

## Error Retry Mechanism

Failures in Selenium actions are automatically retried based on the `retry.maxAttempts` and `retry.delayMs` settings in framework.properties. This reduces flaky test results and improves stability.

## License
This project is licensed under the MIT License. See the [LICENSE](https://github.com/Bala-murugan-tr/selenium-testng-extentreport/blob/main/LICENSE) file for details.