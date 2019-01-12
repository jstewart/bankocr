# BankOCR Kata

This is my solution to the first three user stories of the original [BankOCR Kata](http://codingdojo.org/kata/BankOCR/)

## Running tests

From the project root, run `lein test`. If you wish to develop the project, run `lein test-refresh` for a tighter feedback loop

## Notes

In order to illustrate the approach I took to solving the kata, I made frequent commits. Writing failing tests and specs, then writing the code to conform to the specs and tests.

In order to solve, I started with the simplest things first, then built larger things on top of them. 

User story 3 necessitates creating an output file. I decided to generate this file in the project root rather than pollute the
filesystem of the user that runs the tests.

### User Story 1

Write a program that parses an account number file into actual account numbers.

This requirement is fulfilled in `user-story-1-test`. An input file in `resources/fixtures/accounts.txt` is read, then parsed into account numbers.

### User Story 2

Check account numbers for validity.

This requirement is fulfilled in `user-story-2-test`. The approach I took to creating the checksum for the account numbers is to turn the string into a collection and reduce it with a decrementing index in the calculation.

### User Story 3

Write a file with results of the OCR. Illegible digits are replaced with ?. If there's an incorrect checksum or one of the digits is illegible, it's indicated in a second column

This requirement is fulfilled in `user-story-3-test`. The approach to solve was to read in `resources/fixtures/user-story-3.txt`, read the account numbers, give each number a status, then spit back out to `output.txt`.

## License

Copyright © 2019 Jason Stewart

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
