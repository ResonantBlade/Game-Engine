function print(data)
{
	Java.type("java.lang.System").out.println(data)
}

var config = new Object()
config.developer = false
config.screen_width = 1920
config.screen_height = 1080
config.game_name = "Resonant Blade Game Engine"
config.game_version = Java.type("resonantblade.ge.Properties").getVersion()
config.window_title = config.game_name + " " + config.game_version
config.main_menu_music = null

var audio = new Object()
audio.setVolume = function(amt, channel)
{
	var AudioSystem = Java.type("resonantblade.ge.audio.AudioSystem")
	AudioSystem[channel + "Volume"].volume = amt
}