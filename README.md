# LeagueSim

This app was designed to help me play with scenarios that occur towards the end of the season of a sports league, where
you're trying to figure out what the various qualification scenarios are depending on the results of the last few
matches. In American sports leagues, I do this with playoff qualification; with football (soccer) tournaments, it comes
up at the end of a group phase.

## License

Released as open source under an MIT license; see [LICENSE](LICENSE).

## Dependencies

- Java 21
- JavaFX 21
- snakeYaml 2.4
- gradle 8.5

## Building

Clone the source somewhere, and use the Gradle wrapper to build it:
```
gradlew jlink
```
The jlink plugin will create a shell script at `build/image/bin/leaguesim` that you can use to start the application.

## Getting Started

### Creating a League file

Until I finish adding a bunch more 'create' functionality, you'll need to seed your league with some hand-crafted 
YAML. Check out the contents of `src/test/resources/` for some examples.

A league file contains several sections:
```yaml
league:
  name: My Sports League 2025
  type: ufa-2025
```
The `league` section contains the league `name` (anything you want) and `type`. Recognized values of `type` are in 
`LeagueFactory`.
```yaml
teams:
  - id: BOS
    name: Boston Beaneaters
```
The `teams` entry is a list of team `id` (abbreviation) and `name`. There's no particular limitations on `id`, but it's
expected to be "short". They don't even have to be the same length. Most leagues have some sort of broadcast identifier 
for each team; I recommend you use that.
```yaml
divisions:
  - name: Atlantic
    teams: [BOS, MTL, NY]
```
You can have as many `divisions` as you want. European leagues tend to have one (unless you're tracking the entire 
promotion/relegation pyramid in one file, which there's absolutely no reason to do). American leagues often have 
several. There's no rule preventing teams from playing across divisions, but teams are **only** ranked within a 
division (at least for now).

The list of `teams` uses the `id` (short) field.
```yaml
matchdays: []
```
The `matchdays` entry represents your league's calendar of games. You can leave this empty for now; there's a functional
UI for adding match days (and games) to your league.

You can split up the calendar however you want. If the league's calendar is complex (i.e. Major League Baseball) I
recommend literally doing it by calendar days. If there's an easy way to break up "weeks" or "match days" (in the case
of a tournament) of games that are all being played "at the same time", you can do that. Or whatever works for you.
Match Days are important to the UI because you can scroll through them and edit all the games in a single Match Day all
at once. But, again, there's no restrictions on (for example) the number of times a single team can play during any
particular match day.

### Using the app

Once you have your league file put together, go ahead and start the app and try to load it. I don't guarantee that I've
captured everything that can go wrong during parsing, but hopefully it will at least pop up a dialog box with the 
Exception message. Good luck.

If the division tables come up, you're good to go. Head to the Games menu and pick "Edit Match Days". Click the stack
button to create a new Match Day, give it a name, and hit OK. For the sake of demonstration, close that dialog and go
back to the main window. Make sure your new Match Day is selected in the dropdown.

Now to back to the "Games" menu and pick "Edit Games". Click the stack button to create a new Game, pick the Home and
Away teams, and click OK. Feel free to do that a few more times, and play around with the reordering buttons too.

Once you're back on the main screen, you can fill in the score for your new games. Once you do that, the division tables
should automatically recalculate. Keep in mind that the tables are only showing you the *current* match day and all
previous results. If you have multiple match days with filled out games, you can see this effect by cycling through the
match day dropdown (or using the arrow buttons on either side to move one match day at a time).

Use File > Save (or Ctrl-S) to save any changes you've made to the league file.