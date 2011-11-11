Was concerned that slowdown was from pageToRetrieve queue growing without bounds.  Noticed pattern, usually having a pause at size 141.  Realized that the queue will always be roughly the number of pages visited times the average number of links per page.

Tested the latency between notifyingObservers and actually getting the notification--negligible.

Assuming time issues is due to my connection.

Would be nice to add average size of queues relative to number of pages?  Or at least average size of printBuffer.

2.5 seconds to shut down.