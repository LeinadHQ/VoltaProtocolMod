package voltaprotocol.expand.maps;

import arc.graphics.Color;
import arc.graphics.Gl;
import arc.math.Mathf;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.util.Time;
import mindustry.graphics.Shaders;
import mindustry.graphics.g3d.PlanetMesh;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.type.Planet;

public class VoltaRingMesh extends PlanetMesh {
    static Mat3D mat = new Mat3D();

    public Vec3 tiltAxis = new Vec3(1f, 0f, 0.2f).nor();
    public float tiltDeg = 18f; //Grados de inclinación del anillo (0 = anillo "acostado" sobre el ecuador, 90 = vertical).
    public float rotateSpeed = 0.18f; //velocidad de rotacion.

    public VoltaRingMesh(Planet planet, float radius, float height, float innerFrac, int bands,
                          float tiltDeg, float rotateSpeed, Color color, Color color2) {
        super(planet, RingMeshBuilder.build(radius, height, 140, innerFrac, bands, color, color2), Shaders.clouds);
        this.tiltDeg = tiltDeg;
        this.rotateSpeed = rotateSpeed;
    }

    public VoltaRingMesh() {
    }

    public float relRot() {
        return Time.globalTime * rotateSpeed / 40f;
    }

    @Override
    public void render(PlanetParams params, Mat3D projection, Mat3D transform) {
        if (params.planet == planet && Mathf.zero(1f - params.uiAlpha, 0.01f)) return;

        preRender(params);
        shader.bind();
        shader.setUniformMatrix4("u_proj", projection.val);
        shader.setUniformMatrix4("u_trans", mat
                .setToTranslation(planet.position)
                .rotate(tiltAxis, tiltDeg)
                .rotate(Vec3.Y, planet.getRotation() + relRot()).val);
        shader.apply();
        mesh.render(shader, Gl.triangles);
    }

    @Override
    public void preRender(PlanetParams params) {
        Shaders.clouds.planet = planet;
        Shaders.clouds.lightDir.set(planet.solarSystem.position).sub(planet.position)
                .rotate(tiltAxis, tiltDeg)
                .rotate(Vec3.Y, planet.getRotation() + relRot())
                .nor();
        Shaders.clouds.ambientColor.set(planet.solarSystem.lightColor);
        Shaders.clouds.alpha = params.planet == planet ? 1f - params.uiAlpha : 1f;
    }
}