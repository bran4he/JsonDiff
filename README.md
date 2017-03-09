##Json Diff Tool

### BACKGROUND
as Data Migration of policy in insurance industry, we need compare 01 json structure of policy in our core system, and 02 json structure of policy which extracted from legacy system, and help improve the efficiency of DM task.


### HOW-TO-USE


- prepare

> cd 'root of poejct_dir'

- compile and package

> mvn clean package

- add json_file01 json_file02 in the same folder of jar

- add param and execute

> java -jar jsonDiff.jar json_file01 json_file02

### SAMPLE
[json_file01](/compare01.json)

[json_file02](/compare02.json)

[resut excel file ](/compare01.json_vs_compare02.json1489066399274.xlsx)