##Json Diff Tool

### BACKGROUND
as Data Migration of policy in insurance industry, we need compare 01 json structure of policy in our core system, and 02 json structure of policy which extracted from legacy system, and help improve the efficiency of DM task.


### HOW-TO-USE


- compiler and package jar
 
```
cd JsonDiff
mvn clean package
```

- execute

```
/*copy json_file01 and json_file02 to jar folder*/
java -jar jsonDiff.jar json_file01 json_file02
/* or save log to app.log*/
java -jar jsonDiff.jar json_file01 json_file02 > app.log

```

- result

> {json_file01}_vs_{json_file02}_{currentmils}.xlsx

### SAMPLE
[json_file01](/compare01.json)

[json_file02](/compare02.json)

[resut excel file ](/compare01.json_vs_compare02.json_1489066399274.xlsx)