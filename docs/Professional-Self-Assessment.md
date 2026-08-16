# CS 499 Professional Self-Assessment

## Introduction

I began the Computer Science program at Southern New Hampshire University in the fall of 2024. Throughout the program, I have developed a broader understanding of software development by working with programming languages, object-oriented programming, databases, algorithms and data structures, software design, mobile application development, and cybersecurity concepts. Each course provided an opportunity to build upon previous knowledge while developing stronger technical and problem-solving skills.

One of the most valuable aspects of the program was learning how individual programming concepts contribute to the development of a complete computing solution. Rather than viewing programming, databases, algorithms, and software design as completely separate areas, I learned how they interact within an application. The CS-499 capstone allowed me to apply these concepts to an existing project and evaluate how it could be improved rather than simply creating a new application from the beginning.

Three areas that have been particularly important to my development are object-oriented programming and software design, database development using SQL and SQLite, and algorithms and data structures. Object-oriented programming taught me how to organize software into reusable components and how changes to one component can affect the rest of an application. Database development taught me how information should be structured, stored, retrieved, and maintained. Algorithms and data structures taught me to think about how information is represented and processed so that an application can perform useful operations efficiently.

## Development Through the Capstone

For my capstone project, I selected an Android Weight Tracking application originally developed for CS-360: Mobile Architecture and Programming. I chose this artifact because it provided opportunities to demonstrate growth across all three areas of the capstone: software engineering and design, algorithms and data structures, and databases.

One of the most important skills demonstrated through the capstone was my ability to evaluate an existing software solution and identify areas where it could be improved. Instead of treating the original application as a finished product, I examined its structure, functionality, data representation, and user experience. This process required me to consider not only whether the application worked, but whether the underlying implementation was organized in a way that would make the application easier to maintain and expand.

The software engineering enhancements allowed me to improve the organization and usability of the application. I refined the dashboard and application flow, improved input handling, and reorganized portions of the application so that responsibilities were more clearly separated between activities, data structures, and database operations. These changes helped me better understand the importance of maintainability and user-centered design.

The algorithms and data structures enhancement provided an opportunity to improve how weight information was represented and processed. I changed weight values from formatted strings into numeric values so that the application could perform calculations directly on the underlying data. I also improved the organization of weight history and implemented a progress calculation that compares the user's current weight with their starting weight and goal weight. The progress calculation includes validation for conditions such as missing data, invalid values, and situations where a meaningful percentage cannot be calculated.

This enhancement taught me that algorithm development is not simply about writing a mathematical formula. I had to determine what information the algorithm required, how that information should be represented, and how the application should respond when the required information was unavailable or invalid. This reinforced the relationship between data structures and algorithms and showed me how decisions made earlier in development can affect later functionality.

The database enhancement provided another significant area of growth. The original application already used SQLite to store users, weight entries, and goal information. I expanded the database by adding a `user_profiles` table containing information such as the user's display name and preferred weight unit. I also implemented database versioning and migration support so that the new database structure could be introduced without unnecessarily deleting existing user information.

I further improved the database helper by creating reusable methods for retrieving and managing application data. The database is now responsible for operations such as retrieving user profile information, retrieving weight history, obtaining goal information, and supporting additional calculations based on stored data. These changes helped me better understand relational database design and the importance of separating database responsibilities from the user interface.

## Alignment With Course Outcomes

The capstone allowed me to demonstrate progress toward all five CS-499 course outcomes, although some outcomes were more directly represented by the artifact than others.

**Course Outcome 1: Employ strategies for building collaborative environments that enable diverse audiences to support organizational decision-making in the field of computer science.**

Although the capstone was primarily an individual development project, I demonstrated this outcome through the professional documentation and communication surrounding the project. The code review, narratives, and self-assessment required me to explain technical decisions in a way that could be understood by audiences beyond someone directly reading the source code. This helped me recognize that effective software development includes communicating technical information clearly to others.

**Course Outcome 2: Design, develop, and deliver professional-quality oral, written, and visual communications that are coherent, technically sound, and appropriately adapted to specific audiences and contexts.**

The capstone strongly supports this outcome through the code review and written narratives. I had to explain why enhancements were selected, how they were implemented, what challenges were encountered, and how the resulting changes demonstrated my development as a computer science student. The process improved my ability to communicate technical concepts while providing appropriate context for someone reviewing my work.

**Course Outcome 3: Design and evaluate computing solutions that solve a given problem using algorithmic principles and computer science practices and standards appropriate to its solution while managing the trade-offs involved in design choices.**

This outcome is demonstrated through the algorithms and data structures enhancements. Converting weight information from strings to numeric values, organizing weight history, and calculating progress toward a goal required me to evaluate how data should be represented and processed. I also had to consider edge cases and determine how the application should respond when calculations could not be performed reliably.

**Course Outcome 4: Demonstrate an ability to use well-founded and innovative techniques, skills, and tools in computing practices for the purpose of implementing computer solutions that deliver value and accomplish industry-specific goals.**

This outcome is demonstrated throughout the entire artifact. I used Java, Android development tools, SQLite, object-oriented programming, RecyclerView, database queries, and application architecture techniques to enhance an existing computing solution. Rather than making changes solely for appearance, I focused on improvements that added functionality, improved maintainability, and provided more useful information to users.

**Course Outcome 5: Develop a security mindset that anticipates adversarial exploits in software architecture and designs to expose potential vulnerabilities, mitigate design flaws, and ensure privacy and enhanced security of data and resources.**

Security was an important consideration because the application stores user account information and personal weight data. The project reinforced the importance of protecting user-specific information by associating database records with the appropriate user ID and ensuring that database queries retrieve information for the logged-in user. The project also reinforced that security should be considered throughout application development rather than treated as a separate feature added at the end of development.

## Professional Growth

The most significant change in my skills throughout the Computer Science program has been my ability to approach software development as a process of analysis and evaluation rather than simply writing code to satisfy a requirement. Earlier in my education, I was more focused on getting an application to function. Through additional coursework and the capstone experience, I have learned to ask whether the solution is maintainable, whether the data is represented appropriately, whether the application handles unexpected situations, and whether the design provides value to the intended user.

The capstone also improved my debugging and problem-solving skills. Enhancing an existing application introduced challenges that would not necessarily occur when starting a project from scratch. Changes to a data structure could require corresponding changes in an adapter, activity, database query, or user interface. Adding database functionality required me to consider how existing records would be affected by changes to the schema. Resolving these issues taught me to trace problems across multiple components rather than assuming that the problem existed only in the section of code where an error appeared.

Another important area of growth was learning the importance of separating data, application logic, and presentation. For example, storing a weight as a numeric value rather than including the unit within the stored string allowed the application to use the data for calculations while handling its presentation separately. Similarly, placing database operations within the database helper reduced the need for individual activities to manage SQL operations themselves. These decisions resulted in a cleaner and more maintainable application structure.

## Career Preparation

The skills developed throughout the Computer Science program and demonstrated through this capstone have prepared me for continued work in the technology field. My interests have increasingly focused on software development, mobile applications, and user-centered design because these areas allow me to combine technical problem solving with creativity.

The Elevate application represents this combination particularly well. The project required programming and database knowledge, but it also required consideration of how users interact with the application and how information should be presented. This experience strengthened my interest in developing applications that are not only technically functional but also understandable and useful to their intended users.

The capstone also provided experience that extends beyond the specific Android application. The ability to evaluate an existing system, identify weaknesses, design enhancements, manage data, implement algorithms, test changes, and communicate technical decisions is applicable to many software development environments. These skills provide a foundation that I can continue building upon as I pursue professional opportunities.

## Conclusion

The CS-499 capstone represents the progression of my technical skills throughout the Computer Science program. Beginning with an application created in CS-360, I was able to revisit the project with a more advanced understanding of software engineering, algorithms and data structures, and database development. The process demonstrated that software development does not end when an application becomes functional. Effective developers must continually evaluate their solutions, identify opportunities for improvement, consider the effects of design decisions, and communicate those decisions clearly.

Through this project, I strengthened my ability to work with Java and Android development, SQLite databases, object-oriented programming, data structures, algorithms, application architecture, and user-centered design. More importantly, I developed a stronger understanding of how these areas work together to create a complete computing solution.

The capstone has given me an opportunity to demonstrate not only what I learned during the Computer Science program, but also how I can apply that knowledge to improve an existing software solution. It represents a significant step in my development as a technology professional and provides a foundation for continued growth in software development, mobile application development, and user-centered technology.
