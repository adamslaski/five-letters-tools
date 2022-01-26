# Five Letters Tools

Find best starting words for Wordle like games.

This program is inspired by very addictive games like 
[Wordle](https://www.powerlanguage.co.uk/wordle/), [Wellowordl](https://hellowordl.net/), [Literalinie](https://literalnie.fun/), etc. 

When I play such a game I start with some good words and I follow my scheme until I have enough hits to make a guess. This tool takes as an argument list of 
acceptable words and generates suggestions for starting words. 

## Usage

```
mvn package
./target five-letters-tools-1.0-SNAPSHOT-jar-with-dependencies.jar  path/to/words/file
```
It may takes couple of minutes to calculate the suggestions depending on number of words in the file.

Example output
```
Wall clock: 00:05:32.341
adieu story 1500
adieu story chunk 2000
adieu story chunk pilaf 2300
adieu story chunk pilaf dumpy 2500
```

It reads: computation took 5 minutes and 32 seconds. Best pair of words to start with is _adieu_ and _story_, they have 1500 hits, where one point is given for every identified letter and additional point for identified position. Best third word is _chunk_ etc.