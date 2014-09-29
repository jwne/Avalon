package me.aventium.avalon.regions;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Bounds implements Cloneable {

    protected World world;
    protected final Vector min;
    protected final Vector max;

    public Bounds(World world, Vector min, Vector max) {
        this.world = world;
        this.min = min.clone();
        this.max = max.clone();
    }

    public Bounds(Bounds b) {
        this(b.getWorld(), b.min, b.max);
    }

    @Override
    public Bounds clone() {
        return new Bounds(this);
    }

    public static Bounds unboundedB() {
        return new Bounds(Bukkit.getWorld("world"), new Vector(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY),
                          new Vector(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    }

    public static Bounds empty() {
        return new Bounds(Bukkit.getWorld("world"), new Vector(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY),
                          new Vector(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY));
    }

    public boolean contains(Vector point) {
        return point.isInAABB(this.min, this.max);
    }

    public World getWorld() { return world; }

    public void setWorld(World world) { this.world = world; }

    public Vector getMin() {
        return this.min.clone();
    }

    public Vector getMax() {
        return this.max.clone();
    }

    public BlockVector getBlockMin() {
        return new BlockVector((int) this.min.getX() + .5,
                               (int) Math.max(0, Math.min(255, this.min.getY() + .5)),
                               (int) this.min.getZ() + .5);
    }

    public BlockVector getBlockMax() {
        return new BlockVector((int) this.max.getX() + .5,
                (int) Math.max(0, Math.min(255, this.max.getY() + .5)),
                (int) this.max.getZ() + .5);
    }

    public boolean isEmpty() {
        return min.getX() > max.getX() || min.getY() > max.getY() || min.getZ() > max.getZ();
    }

    public Iterable<BlockVector> getBlocks() {
        final BlockVector min = this.getBlockMin();
        final BlockVector max = this.getBlockMax();

        return new Iterable<BlockVector>() {
            @Override
            public Iterator<BlockVector> iterator() {
                return new Iterator<BlockVector>() {
                    private int x = min.getBlockX(),
                                y = min.getBlockY(),
                                z = min.getBlockZ();

                    private boolean hasNext = x < max.getBlockX() &&
                                              y < max.getBlockY() &&
                                              z < max.getBlockZ();

                    @Override
                    public boolean hasNext() {
                        return hasNext;
                    }

                    @Override
                    public BlockVector next() {
                        if(!this.hasNext()) {
                            throw new NoSuchElementException();
                        }

                        BlockVector b = new BlockVector(x, y, z);

                        if(++x >= max.getBlockX()) {
                            x = min.getBlockX();
                            if(++y >= max.getBlockY()) {
                                y = min.getBlockY();
                                if(++z >= max.getBlockZ()) {
                                    this.hasNext = false;
                                }
                            }
                        }
                        return b;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Vector getCenterPoint() {
        return this.min.clone().add(this.max).multiply(0.5);
    }

    @Override
    public String toString() {
        return "Bounds{min=[" + min.toString() + "],max=[" + max.toString() + "]}";
    }

}
