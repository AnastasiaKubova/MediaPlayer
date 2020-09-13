**This is a sample project "Media Player".**

**This project shows how to:**
- Use MediaPlayer for playing audio files;
- Play audio files in the background;
- Create and use interactive notification;
- Use bottom sheet;
- Use alert dialogs;
- Work with the file system and create a custom file picker for selecting files.

**Player functionality:**
- Select folder with tracks;
- Play/pause/skip next/skip before the track in the app and via notification panel;
- Mix tracks in the playlist;
- Repeat track;
- Scroll track via seek bar;
- Select a track for playing;
- Set track for next playing.

<img src="https://github.com/AnastasiaKubova/MediaPlayer/blob/master/preview/bg.gif?raw=true" width="250" /> <img src="https://github.com/AnastasiaKubova/MediaPlayer/blob/master/preview/filepicker.gif?raw=true" width="250"/> <img src="https://github.com/AnastasiaKubova/MediaPlayer/blob/master/preview/playlist.gif?raw=true" width="250"> <img src="https://github.com/AnastasiaKubova/MediaPlayer/blob/master/preview/viewpager.gif?raw=true" width="250">

**Design patterns:**
- MVVM pattern;
- View Pager;
- Navigation component.

**Main components and libraries:**
- MediaPlayer;
- MediaMetadataRetriever;
- NotificationCompat;
- OnAudioFocusChangeListener;
- Service.

**Know issues**
- Some times the player does not switch to the next track if the track less 10 sec.
