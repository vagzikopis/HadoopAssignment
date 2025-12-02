## Hadoop Assignment
Repository storing the code for two Hadoop Assignments under the scope of Lecture Big Data Technologies.

### Initialization
Copy files to hdfs:
```bash
hdfs dfs -mkdir /input
hdfs dfs -mkdir /output
hdfs dfs -mkdir /input/task_1
hdfs dfs -mkdir /input/task_2
hdfs dfs -mkdir /output/task_1
hdfs dfs -mkdir /output/task_2
hdfs dfs -put ./HadoopAssignment/SherlockHolmes.txt /input/task_1
hdfs dfs -put ./HadoopAssignment/ecoli.txt /input/task_2
```

### Task 1: Numeronyms
Counts the numeronyms found in file `SherlockHolmes.txt`. A numeronym is the transformation of word "shorten" to "s5n".

### Task 2: DNA
Counts all found combinations with length 2,3,4 of DNA components A-G-C-T in a file `ecoli.txt`.

### Deployment
Deployment scripts are used to execute the whole pipeline with `hadoopuser`.