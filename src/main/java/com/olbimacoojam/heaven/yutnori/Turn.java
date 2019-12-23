package com.olbimacoojam.heaven.yutnori;

import com.olbimacoojam.heaven.domain.User;
import com.olbimacoojam.heaven.yutnori.exception.IllegalTurnException;
import com.olbimacoojam.heaven.yutnori.piece.moveresult.MoveResults;
import com.olbimacoojam.heaven.yutnori.yut.Yut;
import com.olbimacoojam.heaven.yutnori.yut.Yuts;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Turn {

    private final YutnoriParticipant yutnoriParticipant;
    private final Yuts thrownYuts;
    private final boolean canThrow;

    private Turn(YutnoriParticipant yutnoriParticipant, Yuts thrownYuts, boolean canThrow) {
        this.yutnoriParticipant = yutnoriParticipant;
        this.thrownYuts = thrownYuts;
        this.canThrow = canThrow;
    }

    public Turn(YutnoriParticipant yutnoriParticipant) {
        this(yutnoriParticipant, new Yuts(), true);
    }

    public Turn saveOneThrow(User thrower, Yut yut) {
        if (canThrow(thrower)) {
            thrownYuts.add(yut);
            return new Turn(yutnoriParticipant, thrownYuts, thrownYuts.isThrowAvailable());
        }
        throw new IllegalTurnException();
    }

    private boolean canThrow(User thrower) {
        return isRightTurn(thrower) && canThrow;
    }

    private boolean isRightTurn(User user) {
        return yutnoriParticipant.isRightThrower(user);
    }

    boolean canMove(User user, Yut yut) {
        return isRightTurn(user) && !canThrow && thrownYuts.contains(yut);
    }

    public Color getTeamColor() {
        return yutnoriParticipant.getColor();
    }

    public Turn next(MoveResults moveResults, YutnoriParticipants yutnoriParticipants) {
        if (moveResults.hasCaught()) {
            return new Turn(yutnoriParticipant, thrownYuts, true);
        }

        if (yutnoriParticipants.isPlaying(yutnoriParticipant) && thrownYuts.isRemaining()) {
            return new Turn(yutnoriParticipant, thrownYuts, false);
        }

        return new Turn(yutnoriParticipants.next(yutnoriParticipant));
    }

    public User getUser() {
        return yutnoriParticipant.getParticipant();
    }

    public void removeYut(Yut yut) {
        thrownYuts.remove(yut);
    }
}
