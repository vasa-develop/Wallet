package com.vasa.vaibhav.example_wallet;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;


/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.2.0.
 */
public class KYC_solc_HackKYC extends Contract {
    private static final String BINARY = "6060604052341561000f57600080fd5b60008054600160a060020a033316600160a060020a0319909116179055610b268061003b6000396000f3006060604052600436106100985763ffffffff7c0100000000000000000000000000000000000000000000000000000000600035041663061b2909811461009d5780632771be55146100c45780636d42b6681461016757806390b45f5f146101a1578063a082b0221461023e578063af3d1726146102a3578063af3f3290146102c5578063cb4f04ad146103cf578063e183d77b14610400575b600080fd5b34156100a857600080fd5b6100c2600160a060020a036004351660243560443561041f565b005b34156100cf57600080fd5b6100c260048035600160a060020a03169060446024803590810190830135806020601f8201819004810201604051908101604052818152929190602084018383808284378201915050505050509190803590602001908201803590602001908080601f016020809104026020016040519081016040528181529291906020840183838082843750949650509335935061046092505050565b341561017257600080fd5b610189600160a060020a03600435166024356104fd565b60405191825260208201526040908101905180910390f35b34156101ac57600080fd5b6101c0600160a060020a036004351661054b565b60405182815260406020820181815290820183818151815260200191508051906020019080838360005b838110156102025780820151838201526020016101ea565b50505050905090810190601f16801561022f5780820380516001836020036101000a031916815260200191505b50935050505060405180910390f35b341561024957600080fd5b6100c260048035600160a060020a03169060248035919060649060443590810190830135806020601f8201819004810201604051908101604052818152929190602084018383808284375094965061063895505050505050565b34156102ae57600080fd5b6100c2600160a060020a03600435166024356106c1565b34156102d057600080fd5b6102e7600160a060020a0360043516602435610725565b604051808060200180602001848152602001838103835286818151815260200191508051906020019080838360005b8381101561032e578082015183820152602001610316565b50505050905090810190601f16801561035b5780820380516001836020036101000a031916815260200191505b50838103825285818151815260200191508051906020019080838360005b83811015610391578082015183820152602001610379565b50505050905090810190601f1680156103be5780820380516001836020036101000a031916815260200191505b509550505050505060405180910390f35b34156103da57600080fd5b6103ee600160a060020a03600435166108bf565b60405190815260200160405180910390f35b341561040b57600080fd5b6103ee600160a060020a03600435166108da565b600160a060020a038316600090815260036020526040902080548291908490811061044657fe5b906000526020600020906002020160010181905550505050565b600160a060020a038416600090815260016020819052604090912080549091810161048b83826108f5565b91600052602060002090600302016000606060405190810160409081528782526020820187905281018590529190508151819080516104ce929160200190610926565b506020820151816001019080516104e9929160200190610926565b506040820151816002015550505050505050565b600160a060020a03821660009081526003602052604081208054829182918590811061052557fe5b906000526020600020906002020190508060000154816001015492509250509250929050565b60006105556109a4565b600160a060020a038316600090815260026020526040812080548290811061057957fe5b90600052602060002090600302019050806000015481600101808054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156106275780601f106105fc57610100808354040283529160200191610627565b820191906000526020600020905b81548152906001019060200180831161060a57829003601f168201915b505050505090509250925050915091565b600160a060020a038316600090815260026020526040902080546001810161066083826109b6565b916000526020600020906003020160006060604051908101604090815286825260208201869052600190820152919050815181556020820151816001019080516106ae929160200190610926565b5060408201518160020155505050505050565b600160a060020a03821660009081526003602052604090208054600181016106e983826109e2565b91600052602060002090600202016000604080519081016040528481526000602082015291905081518155602082015181600101555050505050565b61072d6109a4565b6107356109a4565b600160a060020a038416600090815260016020526040812080548291908690811061075c57fe5b9060005260206000209060030201905080600001816001018260020154828054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561080e5780601f106107e35761010080835404028352916020019161080e565b820191906000526020600020905b8154815290600101906020018083116107f157829003601f168201915b50505050509250818054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156108aa5780601f1061087f576101008083540402835291602001916108aa565b820191906000526020600020905b81548152906001019060200180831161088d57829003601f168201915b50505050509150935093509350509250925092565b600160a060020a031660009081526001602052604090205490565b600160a060020a031660009081526003602052604090205490565b815481835581811511610921576003028160030283600052602060002091820191016109219190610a0e565b505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061096757805160ff1916838001178555610994565b82800160010185558215610994579182015b82811115610994578251825591602001919060010190610979565b506109a0929150610a49565b5090565b60206040519081016040526000815290565b815481835581811511610921576003028160030283600052602060002091820191016109219190610a63565b815481835581811511610921576002028160020283600052602060002091820191016109219190610a93565b610a4691905b808211156109a0576000610a288282610ab3565b610a36600183016000610ab3565b5060006002820155600301610a14565b90565b610a4691905b808211156109a05760008155600101610a4f565b610a4691905b808211156109a0576000808255610a836001830182610ab3565b5060006002820155600301610a69565b610a4691905b808211156109a05760008082556001820155600201610a99565b50805460018160011615610100020316600290046000825580601f10610ad95750610af7565b601f016020900490600052602060002090810190610af79190610a49565b505600a165627a7a72305820047b60c04cfd9c9051ba16936b3ef42f7bc88507dc01419d703952f2a8872b0f0029";

    protected KYC_solc_HackKYC(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected KYC_solc_HackKYC(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<TransactionReceipt> UpdateRequestStatus(String UserAddress, BigInteger OrgCode, BigInteger ApprovalStatus) {
        Function function = new Function(
                "UpdateRequestStatus", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(UserAddress), 
                new org.web3j.abi.datatypes.generated.Uint256(OrgCode), 
                new org.web3j.abi.datatypes.generated.Uint256(ApprovalStatus)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> AddUser(String UserAddress, String FullName, String EmailID, BigInteger MobileNo) {
        Function function = new Function(
                "AddUser", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(UserAddress), 
                new org.web3j.abi.datatypes.Utf8String(FullName), 
                new org.web3j.abi.datatypes.Utf8String(EmailID), 
                new org.web3j.abi.datatypes.generated.Uint256(MobileNo)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple2<BigInteger, BigInteger>> ViewRequest(String UserAddress, BigInteger RequestIndex) {
        final Function function = new Function("ViewRequest", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(UserAddress), 
                new org.web3j.abi.datatypes.generated.Uint256(RequestIndex)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple2<BigInteger, BigInteger>>(
                new Callable<Tuple2<BigInteger, BigInteger>>() {
                    @Override
                    public Tuple2<BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple2<BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteCall<Tuple2<BigInteger, String>> viewAadhar(String UserAddress) {
        final Function function = new Function("viewAadhar", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(UserAddress)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}));
        return new RemoteCall<Tuple2<BigInteger, String>>(
                new Callable<Tuple2<BigInteger, String>>() {
                    @Override
                    public Tuple2<BigInteger, String> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple2<BigInteger, String>(
                                (BigInteger) results.get(0).getValue(), 
                                (String) results.get(1).getValue());
                    }
                });
    }

    public RemoteCall<TransactionReceipt> AddAadhar(String UserAddress, BigInteger AadharNo, String DOB) {
        Function function = new Function(
                "AddAadhar", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(UserAddress), 
                new org.web3j.abi.datatypes.generated.Uint256(AadharNo), 
                new org.web3j.abi.datatypes.Utf8String(DOB)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> AddRequest(String UserAddress, BigInteger OrgCode) {
        Function function = new Function(
                "AddRequest", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(UserAddress), 
                new org.web3j.abi.datatypes.generated.Uint256(OrgCode)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple3<String, String, BigInteger>> viewUser(String UserAddress, BigInteger UserIndex) {
        final Function function = new Function("viewUser", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(UserAddress), 
                new org.web3j.abi.datatypes.generated.Uint256(UserIndex)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple3<String, String, BigInteger>>(
                new Callable<Tuple3<String, String, BigInteger>>() {
                    @Override
                    public Tuple3<String, String, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple3<String, String, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue());
                    }
                });
    }

    public RemoteCall<BigInteger> getUserLength(String UserAddress) {
        Function function = new Function("getUserLength", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(UserAddress)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> ViewRequestLength(String UserAddress) {
        Function function = new Function("ViewRequestLength", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(UserAddress)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public static RemoteCall<KYC_solc_HackKYC> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(KYC_solc_HackKYC.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<KYC_solc_HackKYC> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(KYC_solc_HackKYC.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static KYC_solc_HackKYC load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new KYC_solc_HackKYC(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static KYC_solc_HackKYC load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new KYC_solc_HackKYC(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }
}
