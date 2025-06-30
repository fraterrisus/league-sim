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
matchdays:
  - name: Round 1
    games: []
```
Here's where you'll enter the league's calendar of games. I recommend leaving the `games` list empty for now; the UI 
has a functional system for adding new games to a matchday, and it's kind of painful to enter by hand.

You can split up the calendar however you want. If the league's calendar is complex, I recommend literally doing it by
calendar days. If there's an easy way to break up "weeks" or "match days" (in the case of a tournament) of games 
that are all being played "at the same time", you can do that. Or whatever works for you. Match Days are important 
to the UI because you can scroll through them and edit all the games in a single Match Day all at once. But, again, 
there's no restrictions on (for example) the number of times a single team can play during any particular match day.  

### Using the app

Once you have your league file put together, go ahead and start the app and try to load it. I don't guarantee that I've
captured everything that can go wrong during parsing, but hopefully it will at least pop up a dialog box with the 
Exception message. Good luck.

If the division tables come up and the dropdown on the right is populated with match days, then you're good to go. 
Head to the Games menu and pick "Add Game" (or Ctrl-Shift-G) to add a game to the current match day. Unfortunately 
there's not currently any way to edit or reorder games, but you can always save the league file, edit it by hand, and
reload.

When you fill in the score for a game (both teams!), the division tables should automatically recalculate. Keep in 
mind that the tables are only showing you the *current* match day and all previous results. You can see this by 
cycling through the match day dropdown (or using the arrow buttons on either side to move one match day at a time).

Use File > Save (or Ctrl-S) to save any changes you've made to the league file.