# Data-Driven Testing (Excel)
The framework’s Excel-driven approach is controlled by the `DataStrategy` enum. You choose one of four strategies to organize and resolve excel test data, then point the framework at your files via properties.

## 1. DataStrategy Enum
```java
package com.trgan.framework.enums;

/**
 * Defines how test data (Excel files) are organized and resolved for each test.
 */
public enum DataStrategy {
    INDIVIDUAL,          // One file per @Test (e.g. testdata/TC01.xlsx)
    MODULAR,             // One file per module/group (e.g. testdata/ABC.xlsx)
    MODULAR_INDIVIDUAL,  // Folders by module/group, files per test (e.g. testdata/ABC/TC01.xlsx)
    SINGLE               // One central file for all tests (e.g. testdata/data.xlsx)
}
```

## 2. Strategy Comparison

| Strategy	|Description	|Example|
|-----------|---------------------|-------|
|SINGLE	|All test data in one Excel file	|testdata/data.xlsx|
|INDIVIDUAL	|One Excel file per test method|	testdata/TC01.xlsx, testdata/TC02.xlsx|
|MODULAR	|One Excel file per module or group	|testdata/Regression.xlsx|
|MODULAR_INDIVIDUAL	|Module folders containing individual test files	|testdata/Regression/TC01.xlsx, testdata/Regression/TC02.xlsx|

## 3. Configuration
In `src/test/resources/framework.properties`, set:

```properties
# Choose one of: SINGLE | INDIVIDUAL | MODULAR | MODULAR_INDIVIDUAL

data.strategy=MODULAR_INDIVIDUAL
```


- `data.strategy` drives how the framework locates and loads the excel file for each TestNG method.

## 4. Folder Structure Examples
### SINGLE
```
src/test/resources/
└── testdata/
    └── data.xlsx
```
### INDIVIDUAL
```
src/test/resources/
└── testdata/
    ├── TC01.xlsx
    ├── TC02.xlsx
    └── TC03.xlsx
```
### MODULAR
module or group name will set in xml file parameter `group`

MODULE NAME
```
src/test/resources/
└── testdata/
    ├── Login.xlsx
    └── Pricing.xlsx
```
GROUP NAME
```
src/test/resources/
└── testdata/
    ├── Regression.xlsx
    └── Smoke.xlsx
```
### MODULAR_INDIVIDUAL
```
src/test/resources/
└── testdata/
    ├── Login/
    │   ├── TC01.xlsx
    │   └── TC02.xlsx
    └── Pricing/
        ├── TC03.xlsx
        └── TC04.xlsx
```
## 5. File Name
For `MODULAR` the file name should be same as group name.

For `MODULAR_INDIVIDUAL`, `INDIVIDUAL` and `SINGLE` the file name should be same as class name containing `@Test`.
