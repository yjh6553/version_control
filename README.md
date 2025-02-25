# Version_Control

Version_Control is a simplified version control system inspired by Git, built in Java. This project is a personal project that demonstrates core concepts of version control including commits, branching, staging, and persistence.

## Overview

Version_Control replicates basic Git functionalities on a smaller scale. The project implements key operations such as repository initialization, adding files, committing changes, and checking out previous versions. The design of Version_Control is based on a comprehensive design document that details the classes, data structures, algorithms, and persistence mechanisms used.

## Project Structure

- **Main.java**  
  The entry point of the program. It handles user input, validates commands, and sets up the necessary persistence structures by initializing the `.version_control` directory (and subdirectories for blobs, staging, branches, and commits).

- **Commit.java**  
  Represents a commit in the repository.  
  - **Key Fields:**  
    - `_message`: The commit message provided by the user.  
    - `_time`: The timestamp (UNIX time) when the commit was made.  
    - `_parent`: A reference to the parent commit (null for the initial commit).  
    - A treemap that tracks file blobs associated with the commit.

- **Staging.java**  
  Manages the staging area for commits.  
  - **Key Fields:**  
    - `_addition`: A treemap mapping file names to their content identifiers (using SHA1).  
    - `_remove`: A treemap tracking files staged for removal.

- **Gitrepo.java**  
  Contains the core repository methods, including:
  - `init()`: Initializes the repository by setting up the required directory structure.
  - `add(String fileName)`: Stages a file for the next commit.
  - `rm()`: Stages a file for removal.
  - `commit(String msg)`: Creates a new commit based on the staged changes.
  - `log()`: Displays the commit history.
  - `checkout()`: Restores files from previous commits or branches.

## Persistence

Version_Control ensures persistence by storing all data in a `.version_control` directory in the current working directory. This directory contains serialized objects for:
- Blobs (file versions)
- Commits
- The staging area
- Branch information

This approach guarantees that the state of the repository is maintained across multiple runs of the program.

## Getting Started

### Prerequisites
- Java Development Kit (JDK) installed.

### Clone the Repository

```bash
git clone https://github.com/your-username/version_control.git
cd version_control
