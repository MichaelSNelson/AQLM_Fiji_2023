/* From https://forum.image.sc/t/help-combining-2-macros-to-obtain-fwhm-from-the-plot-profile-of-a-stack/30178
 *  Example finding length of line-like structures using supergaussian fitting and FWHM
 * 
 * Olivier Burri, Romain Guiet, EPFL-SV-BIOP
 * June 2018 
 * 
 * REQUIRES 
 * 	An open image
 * 	A line ROI drawn across a structure of interest
 * 	A bright structure on a dark background
 * 	
 * OUTPUTS
 * 	Plot of original data + fit
 * 	FWHM value in ImageJ Log in PIXELS
 */

//Run with Image selected and Line tool drawn on part of the image
//Get Line Profile
y = getProfile();
// Create x axis
x = Array.getSequence(y.length);
// Get some stats to initialize the fit
Array.getStatistics(y, ymin, ymax, ymean, ystdDev);
guesses = newArray(ymin, ymax, ymean, ystdDev, 1.0);
// The formula for a supergaussian
superGaussian = "y = a + b * exp(-1*(pow ((x-c)*(x-c), e ) / pow( 2*d*d , e ) ) )"
// Do fitting
Fit.doFit(superGaussian, x,y, guesses);
Fit.plot();
// Compute and display FWHM in the log window
par_d = Fit.p(3);
par_e = Fit.p(4);
FWHM = 2*sqrt(2)*par_d*pow(log(2),1/(2*par_e));
print("FWHM: "+FWHM);

