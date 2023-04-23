#@ ImgPlus img             # input image
#@ OpService ops           # image processing operations
#@ UIService ui            # user interface
#@ Integer iterations(label="Iterations", value=30)           # number of iterations for Richardson-Lucy deconvolution
#@ Float numericalAperture(label="Numerical Aperture", value=1.4)  # numerical aperture of the microscope objective
#@ Integer wavelength(label="Wavelength (nm)", value=550)           # wavelength of light used in imaging in nanometers
#@ Float riImmersion(label="Refractive Index (immersion)", value=1.5)   # refractive index of the immersion medium
#@ Float riSample(label="Refractive Index (sample)", value=1.4)    # refractive index of the sample
#@ Float xySpacing(label="XY Spacing (nm)", value=62.9)            # pixel size of the XY dimensions in nanometers
#@ Float zSpacing(label="Z Spacing (nm)", value=160)               # pixel size of the Z dimension in nanometers
#@ Float pZ(label="Particle/sample Position (um)", value=0)         # particle/sample position in micrometers
#@ Float regularizationFactor(label="Regularization factor", value=0.002)  # regularization factor for Richardson-Lucy deconvolution
#@output ImgPlus psf       # point spread function (PSF) image
#@output ImgPlus result    # deconvolved image

import ij.IJ
import net.imglib2.FinalDimensions
import net.imglib2.type.numeric.real.FloatType

# convert integer wavelength parameter to float
wavelength = wavelength.toFloat()

# convert input image to 32-bit
img_f = ops.convert().float32(img)

# generate synthetic PSF based on input shape
psf_dims = []
for (dim in img.dimensionsAsLongArray()) {
    psf_dims.add(dim)
}
psf_size = new FinalDimensions(psf_dims as long[])
wv = wavelength * 1E-9   # convert wavelength to meters
lateral_res = xySpacing * 1E-9   # convert pixel size to meters
axial_res = zSpacing * 1E-9   # convert pixel size to meters
psf = ops.create().kernelDiffraction(
    psf_size,
    numericalAperture,
    wv,
    riSample,
    riImmersion,
    lateral_res,
    axial_res,
    pZ,
    new FloatType()
)

# deconvolve image using Richardson-Lucy with total variation (TV) regularization
result = ops.deconvolve().richardsonLucyTV(
    img_f,
    psf,
    iterations,
    regularizationFactor
)