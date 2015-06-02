# Garden Path

A simple game based upon the board game of Quoridor

This contains a simple server written in Java and a UI - currently written in Pure Javascript with no external libraries as an experiment and learning exercise

## Server

Implemented in Java it exposes a JSON API endpoint for listing and creating public and private games along with moving players and placing fences on the board.

The game is serialised using the following format:

```
00    01    02    03    04    05    06    07    08    09    
   00    01    02    03    04    05    06    07    08 
10    11    12    13    14    15    16    17    18    19    
   09    10    11    12    13    14    15    16    17 
20    21    22    23    24    25    26    27    28    29    
   18    19    20    21    22    23    24    25    26 
30    31    32    33    34    35    36    37    38    39    
   27    28    29    30    31    32    33    34    35 
40    41    42    43    44    45    46    47    48    49    
   36    37    38    39    40    41    42    43    44 
50    51    52    53    54    55    56    57    58    59    
   45    46    47    48    49    50    51    52    53 
60    61    62    63    64    65    66    67    68    69    
   54    55    56    57    58    59    60    61    62 
70    71    72    73    74    75    76    77    78    79    
   63    64    65    66    67    68    69    70    71 
80    81    82    83    84    85    86    87    88    89    
   72    73    74    75    76    77    78    79    80 
90    91    92    93    94    95    96    97    98    99 
```

Where the numbers 00..99 represent fence posts and 00..80 represent spaces players can be on.

One player starts on square 04 and must get to 72..80, the other starts on 76 and must get to 00..08. The players current position is stored as a simple number.

Fences must be placed between two posts and can be encoded as a single number. For instance Fence.get(84, 86) and Fence.get(8486) are equivalent and both represent a horizontal fence, blocking the player starting on the bottom.

## Client

Not much done on this yet...
