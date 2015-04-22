function print(data)
{
	Java.type("java.lang.System").out.println(data)
}

var config = new Object()
config.developer = false
config.screen_width = 1920
config.screen_height = 1080
config.game_name = "Resonant Blade Visual Novel Engine"
config.game_version = Java.type("resonantblade.vne.Properties").getVersion()
config.window_title = config.game_name + " " + config.game_version
var audio = new Object()
audio.setVolume = function(amt, channel)
{
	var as = Java.type("resonantblade.vne.audio.AudioSystem")
	as[channel + "Volume"].volume = amt
}