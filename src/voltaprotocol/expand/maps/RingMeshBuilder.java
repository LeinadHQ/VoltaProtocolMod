package voltaprotocol.expand.maps;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Mesh;
import arc.graphics.VertexAttribute;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.struct.Seq;

import java.nio.FloatBuffer;

public class RingMeshBuilder {
    private static final boolean packNormals =
            Core.gl30 != null && (Core.app.isMobile() || Core.graphics.getGLVersion().atLeast(3, 3));

    /**
     * @param radius    radio exterior del anillo
     * @param height    "grosor" vertical del anillo (cuanto más chico, más plano se ve)
     * @param segments  cantidad de segmentos alrededor del círculo (más = más redondo)
     * @param innerFrac fracción del radio exterior que define el radio interior (ej: 0.55f = anillo ancho, 0.9f = anillo finito)
     * @param bands     cantidad de bandas CONCÉNTRICAS (círculos paralelos) entre el radio interior y exterior
     * @param color     color principal, alternado banda por banda con color2
     * @param color2    color secundario
     */
    public static Mesh build(float radius, float height, int segments, float innerFrac, int bands, Color color, Color color2) {
        int vertices = segments * bands * 18;

        Seq<VertexAttribute> attributes = Seq.with(
                VertexAttribute.position3,
                packNormals ? VertexAttribute.packedNormal : VertexAttribute.normal,
                VertexAttribute.color
        );

        Mesh mesh = new Mesh(true, vertices, 0, attributes.toArray(VertexAttribute.class));
        mesh.getVerticesBuffer().limit(mesh.getVerticesBuffer().capacity());
        mesh.getVerticesBuffer().position(0);

        FloatBuffer buf = mesh.getVerticesBuffer();
        buf.clear();

        int stride = packNormals ? 5 : 7;
        float[] floats = new float[stride];

        float half = height / 2f;
        float innerR = radius * innerFrac;

        Vec3 normalUp = new Vec3(0, -1, 0);
        Vec3 normalDown = new Vec3(0, 1, 0);

        for (int b = 0; b < bands; b++) {
            float rOuter = Mathf.lerp(innerR, radius, (float) (b + 1) / bands);
            float rInner = Mathf.lerp(innerR, radius, (float) b / bands);

            float col = (b % 2 == 0 ? color : color2).toFloatBits();
            boolean isOutermost = b == bands - 1;
            boolean isInnermost = b == 0;

            for (int i = 0; i < segments; i++) {
                float a1 = (float) i / segments * Mathf.PI2;
                float a2 = (float) (i + 1) / segments * Mathf.PI2;

                Vec3 p1 = new Vec3(Mathf.cos(a1) * rOuter, half, Mathf.sin(a1) * rOuter);
                Vec3 p2 = new Vec3(Mathf.cos(a2) * rOuter, half, Mathf.sin(a2) * rOuter);
                Vec3 p3 = new Vec3(Mathf.cos(a2) * rOuter, -half, Mathf.sin(a2) * rOuter);
                Vec3 p4 = new Vec3(Mathf.cos(a1) * rOuter, -half, Mathf.sin(a1) * rOuter);

                Vec3 p5 = new Vec3(Mathf.cos(a1) * rInner, half, Mathf.sin(a1) * rInner);
                Vec3 p6 = new Vec3(Mathf.cos(a2) * rInner, half, Mathf.sin(a2) * rInner);
                Vec3 p7 = new Vec3(Mathf.cos(a2) * rInner, -half, Mathf.sin(a2) * rInner);
                Vec3 p8 = new Vec3(Mathf.cos(a1) * rInner, -half, Mathf.sin(a1) * rInner);

                vert(buf, floats, p1, normalUp, col);
                vert(buf, floats, p2, normalUp, col);
                vert(buf, floats, p6, normalUp, col);
                vert(buf, floats, p1, normalUp, col);
                vert(buf, floats, p6, normalUp, col);
                vert(buf, floats, p5, normalUp, col);

                vert(buf, floats, p8, normalDown, col);
                vert(buf, floats, p7, normalDown, col);
                vert(buf, floats, p3, normalDown, col);
                vert(buf, floats, p8, normalDown, col);
                vert(buf, floats, p3, normalDown, col);
                vert(buf, floats, p4, normalDown, col);

                if (isOutermost) {
                    Vec3 normalOuter = new Vec3(p1.x, 0, p1.z).nor();
                    vert(buf, floats, p1, normalOuter, col);
                    vert(buf, floats, p2, normalOuter, col);
                    vert(buf, floats, p3, normalOuter, col);
                    vert(buf, floats, p1, normalOuter, col);
                    vert(buf, floats, p3, normalOuter, col);
                    vert(buf, floats, p4, normalOuter, col);
                }

                if (isInnermost) {
                    Vec3 normalInner = new Vec3(-p5.x, 0, -p5.z).nor();
                    vert(buf, floats, p5, normalInner, col);
                    vert(buf, floats, p6, normalInner, col);
                    vert(buf, floats, p7, normalInner, col);
                    vert(buf, floats, p5, normalInner, col);
                    vert(buf, floats, p7, normalInner, col);
                    vert(buf, floats, p8, normalInner, col);
                }
            }
        }

        mesh.getVerticesBuffer().limit(mesh.getVerticesBuffer().position());
        return mesh;
    }

    private static void vert(FloatBuffer buf, float[] floats, Vec3 p, Vec3 normal, float color) {
        floats[0] = p.x;
        floats[1] = p.y;
        floats[2] = p.z;

        if (packNormals) {
            floats[3] = packNormal(normal.x, normal.y, normal.z);
            floats[4] = color;
        } else {
            floats[3] = normal.x;
            floats[4] = normal.y;
            floats[5] = normal.z;
            floats[6] = color;
        }

        buf.put(floats);
    }

    private static float packNormal(float x, float y, float z) {
        int xs = x < -1f / 512f ? 1 : 0;
        int ys = y < -1f / 512f ? 1 : 0;
        int zs = z < -1f / 512f ? 1 : 0;

        int vi =
                zs << 29 | ((int) (z * 511 + (zs << 9)) & 511) << 20 |
                        ys << 19 | ((int) (y * 511 + (ys << 9)) & 511) << 10 |
                        xs << 9 | ((int) (x * 511 + (xs << 9)) & 511);

        return Float.intBitsToFloat(vi);
    }
}