package be.hogent.tarsos.pitch;

import be.hogent.tarsos.util.ConfKey;
import be.hogent.tarsos.util.Configuration;

/**
 * Converts pitch from one unit to another (and back (and back (and back ...))).
 * @author Joren Six
 */
public final class PitchConverter {

    private PitchConverter() {
    }

    /**
     * C-1 = 16.35 Hz.
     */
    private static final double REF_FREQ = Configuration
    .getDouble(ConfKey.absolute_cents_reference_frequency);

    /**
     * Cache LOG 2 calculation.
     */
    private static final double LOG_TWO = Math.log(2.0);

    /**
     * A MIDI key is an integer between 0 and 127, inclusive. Within a certain
     * range every pitch is mapped to a MIDI key. If a value outside the range
     * is given an IllegalArugmentException is thrown.
     * @param hertzValue
     *            The pitch in Hertz.
     * @return An integer representing the closest midi key.
     * @exception IllegalArgumentException
     *                if the hertzValue does not fall within the range of valid
     *                MIDI key frequencies.
     */
    public static int hertzToMidiKey(final Double hertzValue) {
        final int midiKey = (int) Math.round(hertzToMidiCent(hertzValue));
        if (midiKey < 0 || midiKey > 127) {
            throw new IllegalArgumentException("MIDI is only defined between [" + midiKeyToHertz(0) + ","
                    + midiKeyToHertz(127) + "] " + hertzValue + "does not map to a MIDI key.");
        }
        return midiKey;
    }

    /**
     * Calculates the frequency (Hz) for a MIDI key.
     * @param midiKey
     *            The MIDI key. A MIDI key is an integer between 0 and 127,
     *            inclusive.
     * @return A frequency in Hz corresponding to the MIDI key.
     * @exception IllegalArgumentException
     *                If midiKey is not in the valid range between 0 and 127,
     *                inclusive.
     */
    public static double midiKeyToHertz(final int midiKey) {
        if (midiKey < 0 || midiKey > 127) {
            throw new IllegalArgumentException("MIDI keys are values from 0 to 127, inclusive " + midiKey
                    + " is invalid.");
        }
        return midiCentToHertz(midiKey);
    }

    /**
     * Folds the pitch values to one octave. E.g. 1203 becomes 3 and 956 remains
     * 956, -3 is 1197
     * @param hertzValue
     *            a list of double values in cent
     */
    public static double hertzToRelativeCent(final double hertzValue) {
        double absoluteCentValue = hertzToAbsoluteCent(hertzValue);
        // make absoluteCentValue positive. E.g. -2410 => 1210
        absoluteCentValue = absoluteCentValue >= 0 ? absoluteCentValue : Math.abs(1200 + absoluteCentValue);
        // so it can be folded to one octave. E.g. 1210 => 10
        return absoluteCentValue % 1200.0;
    }

    /**
     * This method is not really practical. Maybe I will need it someday.
     * @param relativeCent
     * @return public static double relativeCentToHertz(double relativeCent){ if
     *         (relativeCent < 0 || relativeCent >= 1200) throw new
     *         IllegalArgumentException
     *         ("Relative cent values are values from 0 to 1199, inclusive " +
     *         relativeCent + " is invalid."); int defaultOctave = 5; int offset
     *         = defaultOctave * 1200; return absoluteCentToHertz(relativeCent +
     *         offset); }
     */

    /**
     * The reference frequency is configured. The default reference frequency is
     * 16.35Hz. This is C0 on a piano keyboard with A4 tuned to 440 Hz. This
     * means that 0 cents is C0; 1200 is C1; 2400 is C2; ... also -1200 cents is
     * C-1
     * @param hertzValue
     *            The pitch in Hertz.
     * @return The value in absolute cents using the configured reference
     *         frequency
     */
    public static double hertzToAbsoluteCent(final double hertzValue) {
        double pitchInAbsCent = 0.0;
        if (hertzValue != 0) {
            pitchInAbsCent = 1200 * Math.log(hertzValue / REF_FREQ) / LOG_TWO;
        }
        return pitchInAbsCent;
    }

    /**
     * Returns the frequency (Hz) of an absolute cent value. This calculation
     * uses a configured reference frequency.
     * @param absoluteCent
     *            The pitch in absolute cent.
     * @return A pitch in Hz.
     */
    public static double absoluteCentToHertz(final double absoluteCent) {
        return REF_FREQ * Math.pow(2, absoluteCent / 1200.0);
    }

    /**
     * Converts a frequency in Hz to a MIDI CENT value using
     * <code>(12 � log2 (f / 440)) + 69</code> <br>
     * E.g.<br>
     * <code>69.168 MIDI CENTS = MIDI NOTE 69  + 16,8 cents</code><br>
     * <code>69.168 MIDI CENTS = 440Hz + x Hz</code>
     * @param hertzValue
     *            The pitch in Hertz.
     * @return The pitch in MIDI cent.
     */
    public static double hertzToMidiCent(final double hertzValue) {
        double pitchInMidiCent = 0.0;
        if (hertzValue != 0) {
            pitchInMidiCent = (12 * Math.log(hertzValue / 440) / LOG_TWO) + 69;
        }
        return pitchInMidiCent;
    }

    /**
     * Converts a MIDI CENT frequency to a frequency in Hz.
     * @param midiCent
     *            The pitch in MIDI CENT.
     * @return The pitch in Hertz.
     */
    public static double midiCentToHertz(final double midiCent) {
        return 440 * Math.pow(2, (midiCent - 69) / 12d);
    }
}
