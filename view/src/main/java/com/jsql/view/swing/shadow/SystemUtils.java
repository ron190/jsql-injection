package com.jsql.view.swing.shadow;

import java.awt.HeadlessException;
import java.awt.Toolkit;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jsql.util.LogLevel;

/*
 * Copyright (c) 2009-2013 JGoodies Software GmbH. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JGoodies Software GmbH nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * Provides convenience behavior to determine the operating system
 * and Java version.
 *
 * @author Karsten Lentzsch
 * @version $Revision: 1.5 $
 */
public class SystemUtils {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();
    
    private static final String OS_WINDOWS = "Windows";

    // Internal Constants *****************************************************

    /**
     * The {@code os.name} System Property. Operating system name.<p>
     *
     * Defaults to {@code null}, if the runtime does not have security
     * access to read this property or the property does not exist.
     */
    protected static final String OS_NAME = getSystemProperty("os.name");

    /**
     * The {@code os.version} System Property. Operating system version.<p>
     *
     * Defaults to {@code null}, if the runtime does not have security
     * access to read this property or the property does not exist.
     */
    protected static final String OS_VERSION = getSystemProperty("os.version");

    /**
     * The {@code os.name} System Property. Operating system name.<p>
     *
     * Defaults to {@code null}, if the runtime does not have security
     * access to read this property or the property does not exist.
     */
    protected static final String JAVA_VERSION = getSystemProperty("java.version");

    // Requesting the OS and OS Version ***************************************

    /**
     * Is true if this is Linux.
     */
    public static final boolean IS_OS_LINUX = startsWith(OS_NAME, "Linux") || startsWith(OS_NAME, "LINUX");

    /**
     * True if this is the Mac OS.
     */
    public static final boolean IS_OS_MAC = startsWith(OS_NAME, "Mac OS");

    /**
     * True if this is Solaris.
     */
    public static final boolean IS_OS_SOLARIS = startsWith(OS_NAME, "Solaris");

    /**
     * True if this is Windows.
     */
    public static final boolean IS_OS_WINDOWS = startsWith(OS_NAME, OS_WINDOWS);

    /**
     * True if this is Windows 98.
     */
    public static final boolean IS_OS_WINDOWS_98 = startsWith(OS_NAME, "Windows 9") && startsWith(OS_VERSION, "4.1");

    /**
     * True if this is Windows ME.
     */
    public static final boolean IS_OS_WINDOWS_ME = startsWith(OS_NAME, OS_WINDOWS) && startsWith(OS_VERSION, "4.9");

    /**
     * True if this is Windows 2000.
     */
    public static final boolean IS_OS_WINDOWS_2000 = startsWith(OS_NAME, OS_WINDOWS) && startsWith(OS_VERSION, "5.0");

    /**
     * True if this is Windows XP.
     */
    public static final boolean IS_OS_WINDOWS_XP = startsWith(OS_NAME, OS_WINDOWS) && startsWith(OS_VERSION, "5.1");

    /**
     * True if this is Windows Vista or Server 2008.
     */
    public static final boolean IS_OS_WINDOWS_VISTA = startsWith(OS_NAME, OS_WINDOWS) && startsWith(OS_VERSION, "6.0");

    /**
     * True if this is Windows 7.
     */
    public static final boolean IS_OS_WINDOWS_7 = startsWith(OS_NAME, OS_WINDOWS) && startsWith(OS_VERSION, "6.1");

    /**
     * True if this is Windows 8.
     */
    public static final boolean IS_OS_WINDOWS_8 = startsWith(OS_NAME, OS_WINDOWS) && startsWith(OS_VERSION, "6.2");

    /**
     * True if this is Windows Vista/Server 2008/7/2008 R2/8.
     */
    public static final boolean IS_OS_WINDOWS_6_OR_LATER = startsWith(OS_NAME, OS_WINDOWS) && startsWith(OS_VERSION, "6.");

    // Requesting the Java Version ********************************************

    /**
     * True if this is Java 6. We check for a prefix of 1.6.
     */
    public static final boolean IS_JAVA_6 = startsWith(JAVA_VERSION, "1.6");

    /**
     * True if this is Java 7. We check for a prefix of 1.7.
     */
    public static final boolean IS_JAVA_7 = startsWith(JAVA_VERSION, "1.7");

    /**
     * True if this is Java 7.x or later. We check that it's not 1.6.
     */
    public static final boolean IS_JAVA_7_OR_LATER = !IS_JAVA_6;

    /**
     * True if this is Java 7. We check for a prefix of 1.7.
     * 
     * @since 1.6
     */
    public static final boolean IS_JAVA_8 = startsWith(JAVA_VERSION, "1.8");

    /**
     * True if this is Java 8.x or later.
     * We check that it's neither 1.6 nor 1.7.
     * 
     * @since 1.6
     */
    public static final boolean IS_JAVA_8_OR_LATER = !IS_JAVA_6 && !IS_JAVA_7;

    // Visual Properties ******************************************************

    /**
     * True since Java 6 update 10.
     *
     * @since 1.2
     */
    public static final boolean HAS_MODERN_RASTERIZER = hasModernRasterizer();

    /**
     * True if the Windows XP Look&amp;Feel is enabled.
     *
     * @since 1.2
     */
    public static final boolean IS_LAF_WINDOWS_XP_ENABLED = isWindowsXPLafEnabled();

    /**
     * Is true if this environment's default toolkit reports a screen resolution
     * below 120 dpi.<p>
     *
     * @since 1.2
     */
    public static final boolean IS_LOW_RESOLUTION = isLowResolution();

    // Internal ***************************************************************

    private static final String AWT_UTILITIES_CLASS_NAME = "com.sun.awt.AWTUtilities";

    protected SystemUtils() {
        // Override default constructor; prevents instantiation.
    }

    /**
     * Tries to look up the System property for the given key.
     * In untrusted environments this may throw a SecurityException.
     * In this case we catch the exception and answer an empty string.
     *
     * @param key   the name of the system property
     * @return the system property's String value, or {@code null} if there's
     *     no such value, or an empty String when
     *     a SecurityException has been caught
     */
    protected static String getSystemProperty(String key) {
        
        try {
            return System.getProperty(key);
            
        } catch (SecurityException e) {
            
            LOGGER.log(
                LogLevel.CONSOLE_JAVA,
                String.format("Can't access the System property %s: %s", key, e.getMessage()),
                e
            );
            
            return StringUtils.EMPTY;
        }
    }

    protected static boolean startsWith(String str, String prefix) {
        
        return str != null && str.startsWith(prefix);
    }

    /**
     * Checks and answers whether this Java runtime has a modern rasterizer
     * or not. More precisely this method aims to understand whether a good
     * or poor rasterizer is used. Sun's Java runtime has improved its
     * rasterizer in the 1.6 N series after build 12.
     *
     * @return {@code true} if the AWTUtilities class is available,
     *     {@code false} if this class is not in the class path.
     */
    private static boolean hasModernRasterizer() {
        
        try {
            Class.forName(AWT_UTILITIES_CLASS_NAME);
            
            return true;
            
        } catch (ClassNotFoundException e) {
            
            return false;
        }
    }

    /**
     * Checks and answers whether the Windows XP style is enabled.
     * This method is intended to be called only if a Windows look&feel
     * is about to be installed or already active in the UIManager.
     * The XP style of the Windows look&amp;feel is enabled by default on
     * Windows XP platforms since the J2SE 1.4.2; it can be disabled either
     * in the Windows desktop as well as in the Java runtime by setting
     * a System property.<p>
     *
     * First checks the platform, platform version and Java version. Then
     * checks whether the desktop property <tt>win.xpstyle.themeActive</tt>
     * is set or not.
     *
     * @return true if the Windows XP style is enabled
     */
    private static boolean isWindowsXPLafEnabled() {
        
        return
            IS_OS_WINDOWS
            && Boolean.TRUE.equals(
                Toolkit.getDefaultToolkit().getDesktopProperty("win.xpstyle.themeActive")
            )
            && getSystemProperty("swing.noxp") == null;
    }

    private static boolean isLowResolution() {
        
        try {
            return Toolkit.getDefaultToolkit().getScreenResolution() < 120;
            
        } catch (HeadlessException e) {
            
            LOGGER.log(
                LogLevel.CONSOLE_JAVA,
                String.format("This environment cannot support a display, keyboard, and mouse: %s", e.getMessage()),
                e
            );
            return true;
        }
    }
}
