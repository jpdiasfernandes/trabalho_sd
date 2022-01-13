package server.businesslogic;

import server.businesslogic.excecoes.UnlockException;
import server.businesslogic.excecoes.UnlockSemLockException;

import java.util.*;
import java.util.concurrent.locks.*;

enum Mode {
    IS(0), IX(1), S(2), X(3);

    private int id;
    Mode(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }
}

class ModeState {
    Mode mode;
    int id;
    long tid;

    public ModeState(Mode mode, int id) {
        this.mode = mode;
        this.id = id;
        this.tid = Thread.currentThread().getId();
    }


}

class ModeStateComparator implements Comparator<ModeState> {
   public int compare(ModeState m1, ModeState m2) {
       return m1.id - m2.id;
   }
}
class LockState {
    private int count = 0;
    int current = 0;
    ReadWriteLock rwl = new ReentrantReadWriteLock();
    Lock rl = rwl.readLock();
    Lock wl = rwl.writeLock();
    TreeSet<ModeState> modes = new TreeSet<>(new ModeStateComparator());
    TreeSet<ModeState> modesLocked = new TreeSet<>(new ModeStateComparator());

    public ModeState addMode(Mode mode) {
        ModeState r = new ModeState(mode, count);
        count++;
        current++;
        modes.add(r);
        return r;
    }

    public void removeState(long tid) throws UnlockException {
        for (ModeState ms : modesLocked) {
            if (ms.tid == tid) {
                modesLocked.remove(ms);
                current--;
                return;
            }
        }
        throw new UnlockException();
    }

    public void moveToLocked(long tid) {
        for (ModeState ms : modes) {
            if (ms.tid == tid) {
                modes.remove(ms);
                modesLocked.add(ms);
                return;
            }
        }
    }

    public Mode getMode(long tid) {
        for (ModeState ms : modes) {
            if (ms.tid == tid) return ms.mode;
        }

        for (ModeState ms : modesLocked) {
            if (ms.tid == tid) return ms.mode;
        }
        return null;
    }
}
public class LockManager {
    private GestaoLN gestao;
    Map<Object, LockState> locks;
    private Lock l = new ReentrantLock();
    private Condition c = l.newCondition();

    private  boolean[][] compatibility =  new boolean[][]{
            { true, true, true, false },
            { true, true, false, false },
            { true, false, true, false },
            { false, false, false, false}
    };


    public LockManager(GestaoLN gestao) {
        this.gestao = gestao;
        this.locks = new HashMap<>();
    }

    private Mode getIntencionMode(Mode mode) {
        Mode intencionMode = Mode.IX;
        switch (mode) {
            case S:
                intencionMode = Mode.IS;
                break;
            case X:
                intencionMode = Mode.IX;
                break;
            default:
                break;
        }

        return intencionMode;
    }

    public void lockConta(Utilizador u, Mode mode) {
        l.lock();
        Mode intencionMode = getIntencionMode(mode);

        ILock(gestao, intencionMode);
        // ainda tenho que verificar se posso fazer gestao.contas a meio da execução,
        // mas julgo que sim se for um final
        ILock(gestao.contas, intencionMode);
        lockObj(u, mode);
        l.unlock();
    }

    public void lockContasLN(Mode mode) {
        l.lock();
        Mode intencionMode = getIntencionMode(mode);
        ILock(gestao, intencionMode);
        lockObj(gestao.contas, mode);
        l.unlock();
    }

    public void lockViagem(Viagem v, Mode mode) {
        l.lock();
        Mode intencionMode = getIntencionMode(mode);
        ILock(gestao, intencionMode);
        ILock(gestao.voos, intencionMode);
        lockObj(v, mode);
        l.unlock();
    }

    public void lockVoosLN(Mode mode) {
        l.lock();
        Mode intencionMode = getIntencionMode(mode);
        ILock(gestao, intencionMode);
        lockObj(gestao.voos, mode);
        l.unlock();
    }

    public void unlockConta(Utilizador u) {
        l.lock();
        long id = Thread.currentThread().getId();
        try {
            removeState(gestao, id);
            removeState(gestao.contas, id);
            unlockObj(u, id);
        } catch (UnlockException | UnlockSemLockException e) {
            e.printStackTrace();
        }

        l.unlock();
    }

    public void unlockContasLN() {
        l.lock();
        long id = Thread.currentThread().getId();
        try {
            removeState(gestao, id);
            unlockObj(gestao.contas, id);
        } catch (UnlockException | UnlockSemLockException e) {
            e.printStackTrace();
        }

        l.unlock();
    }

    public void unlockViagem(Viagem v) {
        l.lock();
        long id = Thread.currentThread().getId();
        try {
            removeState(gestao, id);
            removeState(gestao.voos, id);
            unlockObj(v, id);
        } catch (UnlockException | UnlockSemLockException e) {
            e.printStackTrace();
        }

        l.unlock();
    }

    public void unlockVoosLN() {
        l.lock();
        long id = Thread.currentThread().getId();
        try {
            removeState(gestao, id);
            unlockObj(gestao.voos, id);
        } catch (UnlockException | UnlockSemLockException e) {
            e.printStackTrace();
        }

        l.unlock();
    }

    private void unlockObj(Object obj, long tid) throws UnlockException, UnlockSemLockException{
        l.lock();
        LockState ls = locks.get(obj);
        Mode mode = ls.getMode(tid);
        removeState(obj, tid);
        if (ls.current == 0)
            locks.remove(obj);
        l.unlock();
        switch(mode) {
            case S:
                ls.rl.unlock();
                break;
            case X:
                ls.wl.unlock();
                break;
            default:
                break;
        }



    }

    private void removeState(Object obj, long tid) throws UnlockSemLockException, UnlockException{
        l.lock();
        LockState ls = locks.get(obj);
        if (ls == null) throw new UnlockSemLockException();
        ls.removeState(tid);
        c.signalAll();

        l.unlock();
    }


    public void lockObj(Object obj, Mode mode) {
        l.lock();
        ILock(obj, mode);
        LockState ls = locks.get(obj);
        if (ls == null) System.out.println("Não deveria estar a acontecer!!");
        l.unlock();
        switch (mode) {
            case S:
                ls.rl.lock();
                break;
            case X:
                ls.wl.lock();
                break;
            default:
                break;
        }
    }


    public void ILock(Object obj, Mode mode) {
        l.lock();
        LockState ls = locks.get(obj);
        if (ls == null) {
            ls = new LockState();
           locks.put(obj, ls);
        }

        ModeState ms = ls.addMode(mode);

        try {
            while(!executeLockState(ls, ms)) {
                c.await();
            }

            ls.moveToLocked(ms.tid);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        l.unlock();
    }

    private boolean compatibleMode(Mode m1, Mode m2) {
        return compatibility[m1.getID()][m2.getID()];
    }

    private boolean executeLockState(LockState ls, ModeState ms) {
        Set<ModeState> toCheck = ls.modes.headSet(ms);
        Set<ModeState> toCheckLocked = ls.modesLocked;
        for (ModeState mState : toCheck) {
            if (!compatibleMode(ms.mode,mState.mode) && mState.tid != ms.tid) return false;
        }
        for (ModeState mState : toCheckLocked) {
            if (!compatibleMode(ms.mode, mState.mode) && mState.tid != ms.tid) return false;
        }

        return true;
    }

    public void lock(Object obj, Mode mode) {
        l.lock();
        System.out.println("LOCK MODE : " + mode);
        LockState ls = locks.get(obj);
        if (ls == null) {
            ls = new LockState();
            locks.put(obj, ls);
        }

        ls.addMode(mode);
        l.unlock();
        switch (mode) {
            case S :
                 ls.rl.lock();
                 break;
            case X :
                ls.wl.lock();
                break;
            default:
                break;
        }
        System.out.println("Unlocked");
    }

    public void unlock(Object obj) {
        l.lock();
        LockState ls = locks.get(obj);

        Mode mode = ls.getMode(Thread.currentThread().getId());

        System.out.println("MODE : " + mode);
        l.unlock();
        switch (mode) {
            case S :
                ls.rl.unlock();
                break;
            case X:
                ls.wl.unlock();
                break;
            default:
                break;

        }

        System.out.println("Unlocked");
    }
}
