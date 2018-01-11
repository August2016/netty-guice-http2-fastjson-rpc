package example;

import com.google.inject.AbstractModule;

/**
 * Created by liuchengjun on 2018/1/8.
 */
public class ExampleModule extends AbstractModule{
     @Override
     protected void configure() {
          bind(SampleService.class).to(SampleServiceImpl.class);
     }
}
