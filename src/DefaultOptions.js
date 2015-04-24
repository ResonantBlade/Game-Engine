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
config.main_menu_music = null

var audio = new Object()
audio.setVolume = function(amt, channel)
{
	var AudioSystem = Java.type("resonantblade.vne.audio.AudioSystem")
	AudioSystem[channel + "Volume"].volume = amt
}

var style = new Object()
style.dialogue_window = new Object()
style.dialogue_window.background = null
style.dialogue_window.xMin = 0
style.dialogue_window.yMin = config.screen_height - 400
style.dialogue_window.xMax = config.screen_width
style.dialogue_window.yMax = config.screen_height
style.dialogue_window.padding_left = 150
style.dialogue_window.padding_right = 150
style.dialogue_window.padding_top = 0
style.dialogue_window.padding_bottom = 25
style.default = new Object()
style.default.font = null
style.default.size = 22