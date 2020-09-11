This is a sample project "Media Player".

This project shows how to:
- Use MediaPlayer for playing audio files;
- Play audio files in the background;
- Create and use interactive notification;
- Use bottom sheet;
- Use alert dialogs;
- Work with the file system and create a custom file picker for selecting files.

Player functionality:
- Select folder with tracks;
- Play/pause/skip next/skip before the track in the app and via notification panel;
- Mix tracks in the playlist;
- Repeat track;
- Select a track for playing;
- Set track for next playing.

Design patterns: 
- MVVM pattern

Main components and libraries:
- MediaPlayer;
- MediaMetadataRetriever;
- NotificationCompat;
- Service.

Know issues
- Some times the player does not switch to the next track if the track less 10 sec.
- On Android with API less 23 notification with player management is not shown.
- For now, you cant scroll the song with a seek bar;
- Probably for now you can't close player notification :D (just kill app).