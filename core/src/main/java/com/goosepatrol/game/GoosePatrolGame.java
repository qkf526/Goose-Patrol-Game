
package com.goosepatrol.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class GoosePatrolGame extends Game {

    private MainMenuScreen mainMenuScreen;
    private GameplayScreen gameplayScreen;
    private PauseScreen pauseScreen;
    private GameOverScreen gameOverScreen;
    private VictoryScreen victoryScreen;
    private TutorialScreen tutorialScreen;
    private Screen tutorialReturnScreen;

    @Override
    public void create() {

        // Open the Main Menu when the game launches.
        mainMenuScreen = new MainMenuScreen(this);

        setScreen(mainMenuScreen);
    }

    // Called when Play is selected.
    public void startNewGame() {

        // Dispose of any previous match.
        if (gameplayScreen != null) {

            // Hide it first if it is currently active.
            if (getScreen() == gameplayScreen) {
                setScreen(null);
            }

            gameplayScreen.dispose();
        }

        // Create a completely new match.
        gameplayScreen = new GameplayScreen(this);

        // Display the gameplay screen.
        setScreen(gameplayScreen);

        gameplayScreen.beginStartCountdown(); //Begins the countdown between clicking on play and commencing the game

        // Dispose of the old Game Over screen
        // after switching away from it.
        if (gameOverScreen != null) {
            gameOverScreen.dispose();
            gameOverScreen = null;
        }

        // Free the old Victory screen after switching away.
        if (victoryScreen != null) {

            victoryScreen.dispose();
            victoryScreen = null;
        }
    }

    // We'll use this for returning to the Main Menu.
    public void showMainMenu() {

        setScreen(mainMenuScreen);
    }


    // Open the Pause Menu without destroying gameplay.
    public void pauseGame() {

        if (getScreen() != gameplayScreen) {
            return;
        }

        // Create the Pause Menu only once.
        if (pauseScreen == null) {
            pauseScreen = new PauseScreen(this);
        }

        setScreen(pauseScreen);
    }

    // Return to the existing gameplay screen.
    public void resumeGame() {

        if (getScreen() == pauseScreen &&
            gameplayScreen != null) {

            setScreen(gameplayScreen);
        }
    }

    // End the current match and return to Main Menu.
    public void endGameToMenu() {

        setScreen(mainMenuScreen);

        if (gameplayScreen != null) {

            gameplayScreen.dispose();
            gameplayScreen = null;
        }
        if (gameOverScreen != null) {
            gameOverScreen.dispose();
            gameOverScreen = null;
        }
        if (victoryScreen != null) {
            victoryScreen.dispose();
            victoryScreen = null;
        }
    }


    public void showGameOver(GameOverScreen.Reason reason) {

        // Only transition out of active gameplay.
        if (getScreen() != gameplayScreen) {
            return;
        }

        // Create the correct Game Over screen.
        gameOverScreen = new GameOverScreen(
            this,
            reason
        );

        // Display it.
        setScreen(gameOverScreen);
    }


    public void showVictory(
        int secondsRemaining,
        int energyPercentage,
        int itemsUsed,
        int eventsTriggered
    ) {

        // Only allow Victory from active gameplay.
        if (getScreen() != gameplayScreen) {
            return;
        }

        // Create a screen with the final results.
        victoryScreen = new VictoryScreen(
            this,
            secondsRemaining,
            energyPercentage,
            itemsUsed,
            eventsTriggered
        );

        // Display the Victory screen.
        setScreen(victoryScreen);
    }


    public void openTutorial() {

        if (tutorialScreen == null) {
            tutorialScreen = new TutorialScreen(this);
        }

        // Remember the screen we came from.
        tutorialReturnScreen = getScreen();

        setScreen(tutorialScreen);
    }


    public void backFromTutorial() {

        if (getScreen() != tutorialScreen) {
            return;
        }

        if (tutorialReturnScreen != null) {

            Screen previousScreen = tutorialReturnScreen;

            tutorialReturnScreen = null;

            // Return to the same screen instance.
            setScreen(previousScreen);
        }
    }



    @Override
    public void dispose() {

        super.dispose();

        if (gameplayScreen != null) {
            gameplayScreen.dispose();
        }

        if (mainMenuScreen != null) {
            mainMenuScreen.dispose();
        }

        if (pauseScreen != null) {
            pauseScreen.dispose();
        }
        if (gameOverScreen != null) {
            gameOverScreen.dispose();
        }
        if (victoryScreen != null) {
            victoryScreen.dispose();
        }

        if (tutorialScreen != null) {
            tutorialScreen.dispose();
        }
    }

}
