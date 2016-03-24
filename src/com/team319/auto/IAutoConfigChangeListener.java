package com.team319.auto;

public interface IAutoConfigChangeListener {
	public void onChange(AutoConfig config);

	public void onConfigException(AutoConfigException exception);
}
