# Minechell
Official Bukkit plugin for the EFSC

## Features
- Sit: Right-click on a chair to take a seat!
    - Must use empty hand on the top-facing part of a block
    - A chair is a bottom slab or bottom stair with at least one adjacent sign or trapdoor
    - A chair must have a solid block (that you could stand on) underneath, and a non-opaque block above.
    - 1-second cooldown on sitting in a chair
- Hat: Put any item on your head!
    - Use /hat while holding the item in your main hand
    - If you're already wearing something on your head, it'll be swapped with your hand
- Invisible ItemFrames & Armor Stands: Shoot an invis arrow to make them invisible!
    - The ItemFrame must be filled & ArmorStand must be carrying 1 item (armor or hands) for this to work
    - Use a splash water bottle to make them visible again! Also works on other entities!
    - If you remove all visible items, the invisible ItemFrame/ArmorStand will break off
- Armor Stands: Arms & Poses!
    - Sneak + Use 2 sticks to add arms to an armor stand in the world
    - Sneak + Use shears to break the arms off again
    - Sneak + Use a pickaxe to remove the armor stand base plate
    - Sneak + Use a smooth stone slab to add the base plate again
    - Sneak + Use a music disc to set the armor stand pose! Discs are not consumed.
- Tag: TODO explanation
- Head: TODO explanation

## TODO
- [ ] if saddle on head, then players can right click to ride. sneak-right-click the air to throw them off
- [ ] add some kind of wrench (debug stick), limited to only stairs/fences/etc
- [ ] tag: clear glowing effect when untagged
- [ ] sit: don't allow if block above is opaque/will suffocate you
- [ ] chat channels
- [ ] /yoink and /unyoink
- [ ] Tag: require shown on dynmap
- [ ] Tag scoreboard: who's currently it, how long they've been it
- [ ] markdown syntax in chat messages
- [ ] Trident retrieve items
