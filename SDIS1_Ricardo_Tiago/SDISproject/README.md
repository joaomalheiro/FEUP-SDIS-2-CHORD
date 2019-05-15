To run the project, first it's required to be on the project directory, then, the user must create the rmi registry using "sh scripts/registry.sh".
After this the user is ready to startup the peers, we have 4 scripts for peer startup, being X the number of the peer (1 to 4) do "sh scripts/peerX.sh".
To run the peers on the enhanced version just add "ENH-" before the name of the script. After this the peers should be running. 
We have created some scripts for the various actions that should be runned in the same way as the previous ones:
- Backup
	- backup-1chunk			backup of img2, which as only 1 chunk from peer1
	- backup-mchunk			backup of img, which as only multiple chunks from peer1
	- ENH-backup			backup of img, which as only multiple chunk from peer1 with enhancements activated
- Restore	
	- restore			restore of img from peer1
- Delete
	- delete			delete of img from peer1
	- ENH-delete			delete of img from peer1 with enhancements
- Reclaim
	- reclaim			reclaim space for peer1, 400kb
- State
	- state-peer1			state for peer1
	- state-peer2			state for peer2