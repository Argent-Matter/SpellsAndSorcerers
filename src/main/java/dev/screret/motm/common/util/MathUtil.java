package dev.screret.motm.common.util;

import net.minecraft.world.phys.Vec3;

import org.joml.Vector3fc;

public class MathUtil {

    /**
     * @param vec    The vector to reflect.
     * @param normal The vector to reflect along.
     * @return {@code vec}, reflected along the {@code normal} vector.
     */
    public static Vec3 reflectVec3(Vec3 vec, Vec3 normal) {
        double x = normal.x();
        double y = normal.y();
        double z = normal.z();
        double dot = Math.fma(vec.x, x, Math.fma(vec.y, y, vec.z * z));

        double nX = vec.x - (dot + dot) * x;
        double nY = vec.y - (dot + dot) * y;
        double nZ = vec.z - (dot + dot) * z;
        return new Vec3(nX, nY, nZ);
    }

    /**
     * @param vec    The vector to reflect.
     * @param normal The vector to reflect along.
     * @return {@code vec}, reflected along the {@code normal} vector.
     */
    public static Vec3 reflectVec3(Vec3 vec, Vector3fc normal) {
        return new Vec3(vec.toVector3f().reflect(normal));
    }
}
