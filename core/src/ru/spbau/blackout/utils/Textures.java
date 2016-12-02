package ru.spbau.blackout.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

import static com.badlogic.gdx.graphics.Texture.TextureFilter;
import static com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;


public class Textures {
    /**
     * Parameters for loading textures with mipmap and antialiasing.
     *
     * <p>For some mystic reasons, sometimes loading with mipmap is necessary
     * in order to get a good antialiasing.
     */
    public static final TextureParameter PARAMETER_MIP_MAP_AA = new TextureParameter();
    static {
        PARAMETER_MIP_MAP_AA.genMipMaps = true;
        PARAMETER_MIP_MAP_AA.minFilter = TextureFilter.MipMapLinearLinear;
        PARAMETER_MIP_MAP_AA.magFilter = TextureFilter.MipMapLinearLinear;
    }

    /**
     * Parameters for loading textures without mipmap and with antialiasing.
     *
     * <p>For some mystic reasons, sometimes loading with mipmap is necessary
     * in order to get a good antialiasing.
     */
    public static final TextureParameter PARAMETER_AA = new TextureParameter();
    static {
        PARAMETER_AA.genMipMaps = false;
        PARAMETER_AA.minFilter = TextureFilter.Linear;
        PARAMETER_AA.magFilter = TextureFilter.Linear;
    }


    /**
     * Use this function to load textures with mipmap and antialiasing.
     *
     * <p>For some mystic reasons, sometimes loading with mipmap is necessary
     * in order to get a good antialiasing.
     */
    public static void loadMipMapAA(String name, AssetManager assets) {
        assets.load(name, Texture.class, PARAMETER_MIP_MAP_AA);
    }

    /**
     * Use this function to load textures with mipmap and with antialiasing.
     *
     * <p>For some mystic reasons, sometimes loading with mipmap is necessary
     * in order to get a good antialiasing.
     */
    public static void loadAA(String name, AssetManager assets) {
        assets.load(name, Texture.class, PARAMETER_AA);
    }

    /**
     * Use this function to load textures without mipmap and antialiasing.
     * Sometimes it can notably improve performance.
     */
    public static void loadFast(String name, AssetManager assets) {
        assets.load(name, Texture.class);
    }
}
