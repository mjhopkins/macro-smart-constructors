# Macro-based smart constructors in Scala

Code and [slides](http://mjhopkins.github.io/macro-smart-constructors/) for my [ScalaSyd](http://www.meetup.com/scalasyd/) talk.

-------------------------

Smart constructors are a nice pattern for building data types that need extra validation or calculation.

But in the case when we're building our data type from statically known values, they leave us with two annoyances:

• it's more clunky

• we have a run-time test for something that's known at compile time. 

We'll investigate how Scala macros can help us address these problems while retaining safety.