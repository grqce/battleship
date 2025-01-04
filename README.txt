
===================
=: Core Concepts :=
===================

  1. Collections - I used a LinkedList to allow players to undo ship placements as they set up the board and a TreeMap
  to store the past games. For the LinkedList, each ship placement is added to the list, and players can remove the most recent placement.
  This was a appropriate choice of Collection because there is no need to have a key and value and undoing aligns with the last in first out
  functionality of LinkedLists. For the TreeMap, I needed to use the name of the game as a key so that I could retrieve a specific game
  from multiple saved. I brainstormed using a LinkedList for undoes after my feedback.

  2. 2D Arrays - I used 2D Arrays to represent the Battleship boards of both players and Integers to describe the state of
  each cell on the board. Using 2D Arrays was a good fit because the Battleship board itself is a 2D grid so elements can be
  accessed easily.

  3. JUnit Testing - The 2D array is the core of the Battleship game's state and I used JUnit tests to ensure that the board is
  updated correctly when ships are placed or moves are made, winning conditions are correct, and that all movements are valid.
  JUnit Testing allows for isolated testing of individual game methods.

  4. File I/O - I used File I/O to enable saving and loading the game state. This allows players to pause and resume
  the game by writing the game state to a file and reading and reconstructing it later. I used BufferedWriter and BufferedReader
  and handled exceptions to make sure the game doesn't crash if a file operation fails.

=========================
=: Implementation :=
=========================

  Battleship:
  - (Main game state logic) Maintains the boards for both players
  - Keeps track of whose turn it is, whether ships are currently being placed, how many ships have been placed, and if the game is over.
  - Offers methods for placing ships, undoing & confirming placements, switching turns, and attacking.
  - Checks if the game is over

  RunBattleship:
  - Creates the main window and sets up a CardLayout to switch between different panels/states.
  - Creates Help bar shown throughout the application.
  - Uses the Battleship model and passes it to the view components so they can update the game’s state.
  - Integrates saving/loading and instructions via menu items.

  MenuPanel:
  - Allows the user to start a new game, load a previously saved game, or view instructions.
  - Prompts the user for a game name when starting a new game.

  PlacementPanel:
  - Displays a grid for the current player to place their ships.
  - Shows buttons for confirming placement, undoing the last move, and rotating ship orientation.

  AttackPanel:
  - Displays the board on which the current player can make attacks (clicking cells to hit or miss).
  - Updates the board visuals based on hits and misses returned by the Battleship model.
  - Shows status updates, such as which player’s turn it is, the outcome of attacks (hit or miss), and remaining ships.
  - Switches turns

  GameOverPanel:
  - Shows which player won and provides a button to return to the main menu.

  GameSave:
  -Saves current turn, if they finished placing, if the game is over, ships placed count, and the boards for both players
   to a text file with each line representing a row of 10 cells.
  -Loads a saved game back into a Battleship instance, allowing the user to resume where they left off.

  Move:
  - Helper function that tracks which player made the placement move with start coordinates, ship length, and orientation.
  - Enables the undo operation
