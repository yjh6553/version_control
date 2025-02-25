# Gitlet Design Document
author: Joshua Yoo

## Design Document Guidelines

Please use the following format for your Gitlet design document. Your design
document should be written in markdown, a language that allows you to nicely 
format and style a text file. Organize your design document in a way that 
will make it easy for you or a course-staff member to read.  

## 1. Classes and Data Structures

### Main.java
This class is a entry point of the program. It implements methods to set up 
persistence with `init` to create `.gitlet` folder at current working directory.
Also, it will create `blob`, `staging`, `branches` and `commits` in `.gitlet` to
save serialized files accordingly for other methods that supports from Gitlet.
It set up the basic persistence for this program.

### Commit.java
This class contains basic elements of commit. It also pointed to the parent commit and naturally 
make a tree. 

####Fields
1. `_message` is a commit message when user input message.
2. `_time` : Data type is Date, and gives UNIX time when user call this method.
3. `_parent` : It points to the parent commit. If it is initial commit, then it is null.
4. It also contains the treemap of blob(contents of files) that each commit should track.

###Staging.java
It has two treemaps that has file name as a key value and string of contents that serialized by SHA1
as a value. We will use this class for tracking the contents of any files for each commit.

###Gitrepo.java
This class will have all the method that should Gitlet should have. Main.java focus on 
input arguments from users and its validity with command. 
 


It implements method to set up persistance
Include here any class definitions. For each class list the instance
variables and static variables (if any). Include a ***brief description***
of each variable and its purpose in the class. Your explanations in
this section should be as concise as possible. Leave the full
explanation to the following sections. You may cut this section short
if you find your document is too wordy.


## 2. Algorithms

This is where you tell us how your code works. For each class, include
a high-level description of the methods in that class. That is, do not
include a line-by-line breakdown of your code, but something you would
write in a javadoc comment above a method, ***including any edge cases
you are accounting for***. We have read the project spec too, so make
sure you do not repeat or rephrase what is stated there.  This should
be a description of how your code accomplishes what is stated in the
spec.


The length of this section depends on the complexity of the task and
the complexity of your design. However, simple explanations are
preferred. Here are some formatting tips:

* For complex tasks, like determining merge conflicts, we recommend
  that you split the task into parts. Describe your algorithm for each
  part in a separate section. Start with the simplest component and
  build up your design, one piece at a time. For example, your
  algorithms section for Merge Conflicts could have sections for:

   * Checking if a merge is necessary.
   * Determining which files (if any) have a conflict.
   * Representing the conflict in the file.
  
* Try to clearly mark titles or names of classes with white space or
  some other symbols.

###Main.java
1. `File cwd` is a current working directory where user wants to start Gitlet.
2. main(Stringp[] args): This will take commands for Gitlet and its argument. If arguments is not correct, then print out error accordingly.
3. validateNumArgs(String cmd, String[] args, int num): This is a helper method of main(String[] args) to check the right number of argument for each command.

###Commit.java
1. String _message : Message when a user calls commit along with message.
2. Date _time : UNIX time when user calls commit.
3. Commit _parent : parents of current commit. If current commit is initial, then this `null`.
4. files : Key value for the blob object(treemap).
5. ID : serialized object and used `sha1` to create unique ID for each commit.


###Gitrepo.java
1. init() : initialized necessary directories. If there is one already in current working directory, print message that already exits in the current directory.
2. add(String fileName) : Add a file to Staging Area. Check contents of file to make sure there is an update or not.
3. rm() : Remove a file from staging area. If current commit tracked the file, stage for removal and remove from current working directory if users has not already done so.
4. log() : It prints commit's `ID`, `date`, and `message` from head commit to initial commit.
5. commit(String msg) : Current commit is a head commit and when we create new commit, move head commit to the new commit. 
6. checkout() : 

###Staging.java
1. It is a staging area for commit to check is there any file to `add` or `rm`.
2. `_addition` and `_remove` are treemap that has file name for a key and blob ID for a value.
3. Every time when we call commit, we check `_addition` and `_remove` whether we can make a new commit or not. We can consider this as a database for commit.



## 3. Persistence


###Git

Describe your strategy for ensuring that you don’t lose the state of your program
across multiple runs. Here are some tips for writing this section:

* This section should be structured as a list of all the times you
  will need to record the state of the program or files. For each
  case, you must prove that your design ensures correct behavior. For
  example, explain how you intend to make sure that after we call
       `java gitlet.Main add wug.txt`,
  on the next execution of
       `java gitlet.Main commit -m “modify wug.txt”`, 
  the correct commit will be made.
  
* A good strategy for reasoning about persistence is to identify which
  pieces of data are needed across multiple calls to Gitlet. Then,
  prove that the data remains consistent for all future calls.
  
* This section should also include a description of your .gitlet
  directory and any files or subdirectories you intend on including
  there.

###

## 4. Design Diagram

Attach a picture of your design diagram illustrating the structure of your
classes and data structures. The design diagram should make it easy to 
visualize the structure and workflow of your program.

