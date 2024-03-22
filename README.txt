This app autocompletes a word from a corpus given by a url
usage:

./optolookup [-c <corpus-url>] <prefix>



To run the program you can either use java:
-jar optolookup.jar [-c <corpus-url>] <prefix>
or use the optolookup script:
./optolookup [-c <corpus-url>] <prefix>
you may have to alter the JAVA_EXECUTABLE variable in the script to your Java path and edit permisions with
chmod +x optolookup.sh

I decided to go with a ternary tree to complete the task which should have a search time of O(logN) where N is the
number of words in the corpus.
As the task said search time was the only thing that mattered I considered a trie or simply a hashmap with keys being
every combination of the letters in the corpus but decided the memory requirements and hash collisions would make it an
unrealistic solution for a large corpus.

Notes

Empty strings are not counted in corpusSize

Will accept "" as a prefix and return entire corpus

Returns empty suggestions array if prefix does not match

Improvements

Creating the tree is done with a non recursive function. With more time I would like to make it tail recursive or
possibly perform it without recursion entirely.

As I'm working in scala I felt I should hold to functional principles and use an immutable data structure. This may be
inefficient when creating large trees as the structure needs to be recreated for every letter of every word.

Needs some refactoring. Some functions are public so that I could get tests done quickly. I would avoid this in
production code