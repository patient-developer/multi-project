package consumer.grpc;

import foo.FooServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcClientFactory {

    @Bean
    public FooServiceGrpc.FooServiceBlockingStub fooServiceBlockingStub(GrpcChannelFactory factory){
        return FooServiceGrpc.newBlockingStub(factory.createChannel("foo"));
    }
}
