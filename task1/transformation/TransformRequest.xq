(:: pragma bea:global-element-parameter parameter="$sendMessage1" element="ns1:SendMessage" location="../wsdl/MessageService.wsdl" ::)
(:: pragma bea:global-element-return element="ns0:request" location="../xsd/request.xsd" ::)

declare namespace ns1 = "http://osb.tutorial.epam.com/MessageService";
declare namespace ns0 = "http://kz.cits.osb.com/test/registry";
declare namespace xf = "http://tempuri.org/task1/transformation/TransformRequest/";

declare function xf:TransformRequest($sendMessage1 as element(ns1:SendMessage))
    as element(ns0:request) {
        <ns0:request>
            <ns0:data clazz = "{ data($sendMessage1/request/ns0:data/@clazz) }">
                <ns0:title>{ data($sendMessage1/request/ns0:data/ns0:title) }</ns0:title>
                <ns0:director>{ data($sendMessage1/request/ns0:data/ns0:director) }</ns0:director>
                <ns0:release>{ data($sendMessage1/request/ns0:data/ns0:release) }</ns0:release>
                <ns0:rating>{ data($sendMessage1/request/ns0:data/ns0:rating) }</ns0:rating>
                <ns0:grade>{ data($sendMessage1/request/ns0:data/ns0:grade) }</ns0:grade>
            </ns0:data>
        </ns0:request>
};

declare variable $sendMessage1 as element(ns1:SendMessage) external;

xf:TransformRequest($sendMessage1)
