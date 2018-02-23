package eu.transkribus.core.util;

public class LaCITlabUtils {

	public final static String SEP_SCHEME_KEY = "sepScheme";
	public final static String ROT_SCHEME_KEY = "rotScheme";
	
	private LaCITlabUtils() {}

	/**
	 * null-value: nothing is deleted, regions which already contain lines are skipped and not further processed
	 *
	 */
	public enum DelScheme {
		/**
		 * all regions (and lines) are deleted
		 */
        regions, 
        /**
         * just the lines in given regions are deleted
         */
		lines;
	}
	
	/**
	 * null-value: within a given TextRegion/CellRegion DONT use seperators, if no regions are given, use them
	 *
	 */
	public enum SepScheme {
		/**
		 * uses seperators also within given Regions/Cells
		 */
		always, 
		/**
		 * never uses separator information
		 */
		never;
	}
	
	/**
	 * null value: it is assumed that all present text is 0° oriented
	 *
	 */
	public enum RotScheme {
		/**
		 * it is assumed that the text is homogeneously oriented 
         * (all text lines have the same orientation) - 0°, 90°, 180° OR 270°
		 */
		hom,
		/**
		 * text could be oriented heterogenious, a mix of 0°, 90°, 180° and 270° is possible 
         * -> TextLines with the same orientation are clustered in same TextRegions
		 */
		het;
	}
}
