## anhugli <anhugli@stud.ntnu.no>


## daviros <daviros@stud.ntnu.no>
Throughout the project, I have been primarily responsible for the structural architecture of the application. I set up the modular Maven structure and continuously refactored it to ensure a clean, well-separated, and maintainable architecture. I introduced the WorkoutService and WorkoutClient interface to strengthen the separation between the presentation layer and business logic, ensuring a clean and scalable system design. I also created the structural foundation that made the application a shippable product.
I also had the main responsibility for the UI layer, where I established the overall view structure and navigation flow between screens and implemented the controllers and FXML views for TableView and FormView. Additionally I implemented a dark, responsive CSS-based theme to improve usability and ensure a consistent user experience.
It naturally follows that I also took extra responsibility for code quality. I actively contributed to improving the codebase regarding separation of concerns, readability, and design principles. Furthermore I was highly active in code reviews, providing feedback to maintain a consistent and high-quality codebase.
In documentation, I co-created the sequence diagram for the “Add Workout” flow, co-created the package diagram, and wrote the sustainability reflection, as well as contributed to user stories (US3 specifically) and GitHub organization.

## konradj <konradj@stud.ntnu.no>


## madsnot <madsnot@stud.ntnu.no>
In this project, my main focus was on improving the overall test quality and coverage. I spent much of my time strengthening the TestFX suite for the UI layer, extending existing tests and adding several new ones to cover edge cases, validation, user interactions, and controller logic. This work helped raise the overall test coverage to above 80 percent and made the test suite more stable and reliable.

I also contributed to the core module by implementing the Workout class, including its validation rules and data handling logic. In addition, I introduced the project’s Checkstyle setup, ensuring consistent formatting and helping the team maintain cleaner, more uniform code.

Throughout the development process, I helped refine documentation by co-authoring the sequence diagram and contributing to the project’s package diagram. I was also active in code reviews, where I provided feedback on structure, clarity, and testability to help keep the codebase coherent across team members.

## markrin <markrin@stud.ntnu.no>
During this project, my main contributions were in the core, UI, and documentation modules. I designed and implemented the Exercise domain class and extended the Workout class to include a list of exercises with full validation, JSON serialization, and unique exercise IDs. Also, I updated associated tests to ensure model integrity and prevent duplicate entries. In the UI, I helped build the ExerciseTableView to display and edit general workout information in the same view as the Exercise table. This involved implementing staged editing behavior, improving validation, and resolving several UX and initialization bugs. For this, I also created TestFX UI tests to verify correct user interactions. I also wrote and updated documentation, including persistence.md, teamwork.md, co-created sequence- and package-diagram and created class-diagram-release2/3 alongside class-level Javadoc. 

In addition to my individual work, I was a co-author on several commits contributing to architecture and design documentation, UI enhancements, and code quality improvements. These included creating and updating PlantUML and package diagrams to visualize project structure, improving navigation between main screens, introducing a dedicated UI constants class, and preventing persistent storage of test data during automated UI tests. I also co-authored updates to the Release 2 documentation, adding guidance on quality tooling, modular structure, and diagram conventions.

## trymajo <trymajo@stud.ntnu.no>
My main contributions in this release were centered around the server, backend logic, and related documentation. I spent most of my time improving and maintaining the backend module, ensuring stable communication with the REST API and supporting the frontend team by clarifying data flow and integration needs between the client and server. I also worked on fixing several failing tests and refactoring parts of the backend to improve reliability and maintainability.

Beyond development, I contributed significantly to documentation. I wrote and updated several project documents, including teamwork.md and ai-tools.md, to ensure clarity and consistency across releases. Additionally, I assisted in setting up the Windows packaging process with jpackage and WiX, making the application buildable as a native installer. Overall, I served as a link between frontend and backend work, providing technical guidance, improving test coverage, and helping ensure the project’s backend was both stable and well-documented.


