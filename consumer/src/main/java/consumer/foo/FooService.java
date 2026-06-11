package consumer.foo;

import foo.Foo;
import foo.FooServiceGrpc;
import org.springframework.stereotype.Service;

@Service
public class FooService {

    private final FooServiceGrpc.FooServiceBlockingStub blockingStub;

    public FooService(FooServiceGrpc.FooServiceBlockingStub blockingStub) {
        this.blockingStub = blockingStub;
    }

    void invokeFoo(){
        var request = Foo.FooRequest.newBuilder().setBar("Raise the Bar!").build();
        var _ = blockingStub.fooStuff(request);
    }
}
