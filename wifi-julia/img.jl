using Colors
using Images
using ImageView

img = imread("/home/verizon/Downloads/oie_9125662lNB9IQ.png")
#img = load("/home/verizon/raman/plan.png")
println(size(img))
show(img)
#plan = squeeze(img.data[2,:,:],1);
#plan = squeeze(img.data[:,:,2],3);
plan = img;
show(plan)
println(size(plan))
dimx, dimy = size(plan)  # spatial dimensions
fsize = length(plan)     # full size of the problem
δ = 0.03                 # spatial resolution of each pixel
println(δ)
η_air = 1.                 # refraction index for air
η_concrete = 2.55 - 0.01im  # refraction index for concrete
# the imaginary part conveys the absorption

λ = 0.12                  # for a 2.5 GHz signal, wavelength is ~ 12cm
k = 2π / λ                # k is the wavenumber

println(k)

μ = similar(plan, Complex)
μ[plan .!= 0] = (k / η_air)^2
μ[plan .== 0] = (k / η_concrete)^2;

xs = Array(Int, 5*dimx*dimy)
ys = Array(Int, 5*dimx*dimy)
vs = Array(Complex, 5*dimx*dimy)
i = 1
for x in 1:dimx, y in 1:dimy  #  x=1; y=1
    xm = (x+dimx-2) % dimx + 1
    xp =          x % dimx + 1
    ym = (y+dimy-2) % dimy + 1
    yp =          y % dimy + 1

    xs[i] = sub2ind((dimx,dimy), x, y); ys[i] = sub2ind((dimx,dimy),  x,  y); vs[i] = μ[x,y] - 2*δ^-2
    i += 1
    xs[i] = sub2ind((dimx,dimy), x, y); ys[i] = sub2ind((dimx,dimy), xp,  y); vs[i] = δ^-2
    i += 1
    xs[i] = sub2ind((dimx,dimy), x, y); ys[i] = sub2ind((dimx,dimy), xm,  y); vs[i] = δ^-2
    i += 1
    xs[i] = sub2ind((dimx,dimy), x, y); ys[i] = sub2ind((dimx,dimy),  x, yp); vs[i] = δ^-2
    i += 1
    xs[i] = sub2ind((dimx,dimy), x, y); ys[i] = sub2ind((dimx,dimy),  x, ym); vs[i] = δ^-2
    i += 1
end

S = sparse(xs, ys, vs, fsize, fsize);

f = fill(0. + 0.im, (dimx, dimy))
#f[80:82, 160:162] = 1.0;                  # our Wifi emitter antenna will be there;
f[110:112, 202:204] = 1.0;                  # our Wifi emitter antenna will be there;
#f[300:302, 160:162] = 1.0;                  # our Wifi emitter antenna will be there;

A = reshape(S \ vec(f), dimx, dimy);
E = real(A) .* real(A);
E = conv2(E, ones(5,5)/25 )[3:end-2, 3:end-2];

Ei = iround(min(100, max(1, (int( 1 .+ 100 .* (E .- minimum(E)) / (maximum(E) - minimum(E)) ) ))));
cm = reverse(colormap("oranges"));
Ei[plan .== 0] = 1;

field = Array(Float64, (dimx, dimy, 3))
field[:,:,1] = [ cm[Ei[i]].r for i in 1:fsize ]
field[:,:,2] = [ cm[Ei[i]].g for i in 1:fsize ]
field[:,:,3] = [ cm[Ei[i]].b for i in 1:fsize ]
fim = colorim(permutedims(field, [2, 1, 3]))
imwrite(fim,"raman.png");
println(size(fim))
