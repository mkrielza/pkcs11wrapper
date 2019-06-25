package demo.pkcs.pkcs11.wrapper.speed.keygeneration;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import demo.pkcs.pkcs11.wrapper.speed.ConcurrentSessionBagEntry;
import demo.pkcs.pkcs11.wrapper.speed.Pkcs11Executor;
import iaik.pkcs.pkcs11.Mechanism;
import iaik.pkcs.pkcs11.Session;
import iaik.pkcs.pkcs11.Token;
import iaik.pkcs.pkcs11.TokenException;
import iaik.pkcs.pkcs11.objects.KeyPair;
import iaik.pkcs.pkcs11.objects.PrivateKey;
import iaik.pkcs.pkcs11.objects.PublicKey;

public abstract class KeypairGenExecutor extends Pkcs11Executor {

  private static final Logger LOG =
      LoggerFactory.getLogger(KeypairGenExecutor.class);

  public class MyRunnable implements Runnable {

    public MyRunnable() {
    }

    @Override
    public void run() {
      while (!stop()) {
        try {
          // generate keypair on token
          PublicKey publicKeyTemplate = getMinimalPublicKeyTemplate();

          PrivateKey privateKeyTemplate = getMinimalPrivateKeyTemplate();
          privateKeyTemplate.getSensitive().setBooleanValue(Boolean.TRUE);
          privateKeyTemplate.getPrivate().setBooleanValue(Boolean.TRUE);

          publicKeyTemplate.getToken().setBooleanValue(Boolean.TRUE);
          privateKeyTemplate.getToken().setBooleanValue(Boolean.TRUE);

          byte[] id = new byte[20];
          new Random().nextBytes(id);
          publicKeyTemplate.getId().setByteArrayValue(id);
          privateKeyTemplate.getId().setByteArrayValue(id);

          publicKeyTemplate.getVerify().setBooleanValue(Boolean.TRUE);
          privateKeyTemplate.getSign().setBooleanValue(Boolean.TRUE);

          // netscape does not set these attribute, so we do no either
          publicKeyTemplate.getKeyType().setPresent(false);
          privateKeyTemplate.getKeyType().setPresent(false);

          publicKeyTemplate.getObjectClass().setPresent(false);
          privateKeyTemplate.getObjectClass().setPresent(false);

          ConcurrentSessionBagEntry sessionBag = borrowSession();
          KeyPair keypair;
          try {
            Session session = sessionBag.value();
            keypair = session.generateKeyPair(mechanism, publicKeyTemplate,
                privateKeyTemplate);
          } finally {
            requiteSession(sessionBag);
          }

          // we use here explicitly not the same session.
          sessionBag = null;
          sessionBag = borrowSession();
          try {
            Session session = sessionBag.value();
            session.destroyObject(keypair.getPrivateKey());
            session.destroyObject(keypair.getPublicKey());
          } finally {
            requiteSession(sessionBag);
          }

          account(1, 0);
        } catch (Throwable th) {
          System.err.println(th.getMessage());
          LOG.error("error", th);
          account(1, 1);
        }
      }
    }

  }

  private final Mechanism mechanism;

  public KeypairGenExecutor(String description, long mechnism,
      Token token, char[] pin) throws TokenException {
    super(description, token, pin);
    this.mechanism = new Mechanism(mechnism);
  }

  protected abstract PrivateKey getMinimalPrivateKeyTemplate();

  protected abstract PublicKey getMinimalPublicKeyTemplate();

  @Override
  protected Runnable getTestor() throws Exception {
    return new MyRunnable();
  }

}