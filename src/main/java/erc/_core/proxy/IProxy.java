package erc._core.proxy;

public interface IProxy{
	int getNewRenderType();
	void preInit();
	void init();
	void postInit();
}