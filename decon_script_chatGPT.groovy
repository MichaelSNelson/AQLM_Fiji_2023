// This script takes an input image and generates a synthetic point spread function (PSF) based on the specified parameters.
// It then deconvolves the input image with the generated PSF using the Richardson-Lucy algorithm to produce a result image.

// The following annotations define the input and output parameters for the script, and allow the user to specify values for them when running the script.
// The `ImgPlus` data type represents an image with metadata, and `OpService` and `UIService` are services provided by the ImageJ API.
// `Integer`, `Float`, and `FloatType` are standard Java data types.
//@ ImgPlus img
//@ OpService ops
//@ UIService ui
//@ Integer iterations(label="Iterations", value=30)
//@ Float numericalAperture(label="Numerical Aperture", value=1.4)
//@ Float wavelength(label="Wavelength (nm)", value=550)
//@ Float riImmersion(label="Refractive Index (immersion)", value=1.5)
//@ Float riSample(label="Refractive Index (sample)", value=1.4)
//@ Float xySpacing(label="XY Spacing (nm)", value=62.9)
//@ Float zSpacing(label="Z Spacing (nm)", value=160)
//@ Integer depth(value=0)
//@output ImgPlus psf
//@output ImgPlus result

// The following line imports the ImageJ IJ class, which provides various utility methods for working with images.
import ij.IJ

// The following lines import classes from the ImageJ API that will be used in the script.
import net.imglib2.FinalDimensions
import net.imglib2.type.numeric.real.FloatType

// Convert the input image to a 32-bit floating-point representation, which is required for subsequent processing steps.
img_f = ops.convert().float32(img)

// Generate a synthetic PSF based on the input image shape and the specified parameters.
// The PSF is created using the `kernelDiffraction` function, which generates a diffraction-limited PSF based on the input parameters.
// The lateral and axial resolutions are calculated based on the XY and Z spacings and the wavelength of light used in imaging.
psf_dims = []
for (dim in img.dimensionsAsLongArray()) {
    psf_dims.add(dim)
}
psf_size = new FinalDimensions(psf_dims as long[])
wv = wavelength * 1E-9
lateral_res = xySpacing * 1E-9
axial_res = zSpacing * 1E-9
psf = ops.create().kernelDiffraction(psf_size, numericalAperture, wv, riSample, riImmersion, lateral_res, axial_res, depth, new FloatType())

// Deconvolve the input image with the generated PSF using the Richardson-Lucy algorithm.
// This algorithm iteratively estimates the original image based on the PSF and the observed image.
result = ops.deconvolve().richardsonLucy(img_f, psf, iterations)
